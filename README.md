# frida-carclaims-voice-backend

Quarkus backend for the FRIDA Car Claims voice-to-form feature. Accepts browser-recorded audio, transcribes it with Whisper, and maps spoken content to the [FRIDA Car Claims Data Schema](https://github.com/FRIDA-api/frida-carclaims-frontend/blob/main/claimsOas.yaml) using Qwen via LiteLLM and langchain4j.

## Prerequisites

- Java 21
- Maven (wrapper included)
- LiteLLM API key and base URL (for claims extraction)
- Whisper API key and base URL (for speech-to-text, separate provider)

## Configuration

The backend uses **two independent OpenAI-compatible providers**:

| Provider | Purpose | Env vars |
|----------|---------|----------|
| LiteLLM | Claims field extraction (Qwen3.6-35B-A3B) | `LITELLM_BASE_URL`, `LITELLM_API_KEY`, `CHAT_MODEL`, `LITELLM_TIMEOUT` (default `120s`) |
| whisper.cpp server | Speech-to-text | `WHISPER_BASE_URL` (full `/inference` URL), `WHISPER_API_KEY` (optional) |

Example:

```shell
export LITELLM_BASE_URL=https://litellm-litemaas.apps.prod.rhoai.rh-aiservices-bu.com/v1/
export LITELLM_API_KEY=sk-...
export CHAT_MODEL=Qwen3.6-35B-A3B

# whisper.cpp server — must be the full /inference endpoint, not OpenAI /v1/
export WHISPER_BASE_URL=http://localhost:51060/inference
export WHISPER_API_KEY=foo
```

`WHISPER_BASE_URL` must point at the whisper.cpp **`/inference`** endpoint. The backend uses the whisper.cpp multipart API (`file`, `response_format=json`, `language`), not OpenAI `/v1/audio/transcriptions`. Langchain4j's OpenAI transcription client is not used because it is unsupported in Quarkus and incompatible with whisper.cpp.

Only `WHISPER_BASE_URL` is required at startup. The API key is optional (whisper.cpp does not require auth).

### Model capabilities

| Model | Audio STT | Text/JSON extraction |
|-------|-----------|----------------------|
| Granite-Vision-3.2 | No | Yes (text mode) |
| Qwen2.5-VL-7B-Instruct | No | Yes (text mode) |
| Qwen3.6-35B-A3B | No | Yes (recommended for extraction) |
| Nomic-embed-text-v2-moe | No | No (embeddings only) |
| Whisper | Yes | No |

Vision and text models cannot replace Whisper for audio transcription. Granite-Vision and similar models handle images and text only.

## Running in dev mode

```shell
./mvnw quarkus:dev
```

Dev UI: http://localhost:8080/q/dev/

Request/response logging for the chat model is enabled in dev mode.

## API

### `POST /api/voice/extract`

Transcribes audio and extracts claim fields for the frontend form.

**Content-Type:** `multipart/form-data`

| Part | Required | Description |
|------|----------|-------------|
| `audio` | yes | Audio file (`audio/webm`, `audio/wav`, `audio/mpeg`, etc.) |
| `currentState` | no | JSON string with existing partial `Claimsdata` form state |
| `language` | no | Whisper language hint (default: `de`) |

**Success response (200):**

```json
{
  "transcript": "Der Unfall war am 24. August in Bedburg.",
  "claimsData": {
    "accidentDate": "2019-08-24",
    "accidentCity": "Bedburg"
  }
}
```

Only non-null fields are returned in `claimsData`. The frontend should merge this into its form state.

**Error responses:**

| Status | Cause |
|--------|-------|
| 400 | Missing or empty audio, invalid `currentState` JSON |
| 502 | Whisper transcription or LiteLLM extraction failed |
| 500 | Unexpected server error |

**Example (curl):**

```shell
curl -X POST http://localhost:8080/api/voice/extract \
  -F "audio=@recording.webm;type=audio/webm" \
  -F 'currentState={"accidentCity":"Köln"}' \
  -F "language=de"
```

## Architecture

1. **Speech-to-text** — whisper.cpp server via `POST /inference` (multipart upload)
2. **Schema mapping** — Qwen3.6-35B-A3B via LiteLLM with thinking disabled (`enable_thinking: false`) and manual JSON parsing. The system prompt is built at startup from vendored FRIDA schema resources under `src/main/resources/frida/` (`claimsOas.yaml`, `descriptionClaim.md`, `voice-mapping-hints.md`).
3. **Merge** — Java `ClaimsDataMerger` deep-merges extracted fields into optional `currentState`

### Schema resources

The LLM prompt uses the frontend [claimsOas.yaml](https://github.com/FRIDA-api/frida-carclaims-frontend/blob/main/claimsOas.yaml) as the source of truth for field paths and enums. When the frontend schema changes, refresh the vendored copy:

```shell
curl -sL "https://raw.githubusercontent.com/FRIDA-api/frida-carclaims-frontend/main/claimsOas.yaml" \
  -o src/main/resources/frida/claimsOas.yaml
```

Domain context is taken from [FRIDA-car/descriptionClaim_en.md](https://github.com/FRIDA-api/FRIDA-car/blob/master/descriptionClaim_en.md). Voice disambiguation rules live in `src/main/resources/frida/voice-mapping-hints.md`.

## Packaging

```shell
./mvnw package
java -jar target/quarkus-app/quarkus-run.jar
```

Native build:

```shell
./mvnw package -Dnative
```

## Tests

```shell
./mvnw test
```

Tests mock the service layer; no live LiteLLM or Whisper calls in CI.

## Security

Do not commit API keys. Use environment variables or a local `.env` file. Files matching `*.secret` are gitignored.
