# frida-carclaims-voice-backend

Quarkus backend for the FRIDA Car Claims voice-to-form feature. Accepts browser-recorded audio, transcribes it with Whisper, and maps spoken content to the [FRIDA Car Claims Data Schema](https://github.com/FRIDA-api/frida-carclaims-frontend/blob/main/claimsOas.yaml) using Qwen via LiteLLM and langchain4j.

## Prerequisites

- Java 21
- Maven (wrapper included)
- LiteLLM API key (for claims extraction)
- OpenAI API key (for speech-to-text transcription)

## Configuration

The backend uses **two external API services**:

| Service | Purpose | Env vars |
|---------|---------|----------|
| **LiteLLM** | Claims field extraction (Qwen3.6-35B-A3B) | `LITELLM_BASE_URL`, `LITELLM_API_KEY`, `CHAT_MODEL`, `LITELLM_TIMEOUT` (default `120s`) |
| **OpenAI Whisper** | Speech-to-text transcription | `TRANSCRIPTION_BASE_URL`, `TRANSCRIPTION_API_KEY`, `TRANSCRIPTION_MODEL` (default `whisper-1`) |

Example:

```shell
# LiteLLM for claims extraction
export LITELLM_BASE_URL=https://litellm-litemaas.apps.prod.rhoai.rh-aiservices-bu.com/v1/
export LITELLM_API_KEY=sk-...
export CHAT_MODEL=Qwen3.6-35B-A3B

# OpenAI Whisper for transcription
export TRANSCRIPTION_BASE_URL=https://api.openai.com/v1/
export TRANSCRIPTION_API_KEY=sk-proj-...
export TRANSCRIPTION_MODEL=whisper-1
```

The backend uses OpenAI's `/v1/audio/transcriptions` API endpoint for audio transcription.

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
| `stepKey` | no | Frontend form step id (e.g. `carclaimsDetails`, `driver-a`) — scopes LLM extraction to fields on that page |

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
  -F "language=de" \
  -F "stepKey=carclaimsDetails"
```

## Architecture

1. **Speech-to-text** — OpenAI Whisper API via `POST /v1/audio/transcriptions`
2. **Schema mapping** — Qwen3.6-35B-A3B via LiteLLM with thinking disabled (`enable_thinking: false`), temperature 0.1, and manual JSON parsing. The system prompt is built from vendored FRIDA resources under `src/main/resources/frida/` (`claimsOas.yaml`, `descriptionClaim.md`, `voice-mapping-hints.md`, `step-field-catalog.yaml`, `german-field-synonyms.md`, `voice-extraction-examples.md`). When the frontend sends `stepKey`, only fields for that form step are included in the catalog.
3. **Merge** — Java `ClaimsDataMerger` deep-merges extracted fields into optional `currentState`

### Schema resources

The LLM prompt uses the frontend [claimsOas.yaml](https://github.com/FRIDA-api/frida-carclaims-frontend/blob/main/claimsOas.yaml) as the source of truth for field paths and enums. When the frontend schema changes, refresh the vendored copy:

```shell
curl -sL "https://raw.githubusercontent.com/FRIDA-api/frida-carclaims-frontend/main/claimsOas.yaml" \
  -o src/main/resources/frida/claimsOas.yaml
```

Domain context is taken from [FRIDA-car/descriptionClaim_en.md](https://github.com/FRIDA-api/FRIDA-car/blob/master/descriptionClaim_en.md). Voice disambiguation rules live in `src/main/resources/frida/voice-mapping-hints.md`. German colloquial phrases and few-shot examples are in `german-field-synonyms.md` and `voice-extraction-examples.md`. Step-to-field mappings are in `step-field-catalog.yaml` (derived from the frontend `reverseDtoMapper.ts` step groupings).

**Keeping prompt content up to date:**

1. Re-sync OAS when the frontend schema changes (command above).
2. After voice testing sessions, append anonymized transcript → JSON pairs to `voice-extraction-examples.md`.
3. When UI enum labels change, update `german-field-synonyms.md`.
4. Rebuild the native image so new resources are bundled (`quarkus.native.resources.includes` in `application.properties`).

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
# Unit tests (no API keys needed)
./mvnw test

# Integration tests (requires TRANSCRIPTION_API_KEY)
export TRANSCRIPTION_API_KEY=sk-proj-...
./mvnw test -Dtest=OpenAiTranscriptionIntegrationTest
```

Unit tests mock the service layer; no live API calls in CI. Integration tests require a valid OpenAI API key and make real API calls.

See [TESTING.md](TESTING.md) for detailed testing guide.

## CI: Native image on Quay.io

The workflow [`.github/workflows/native-quay.yml`](.github/workflows/native-quay.yml) runs on pushes to `main`/`master` and on manual dispatch. It:

1. Runs unit tests
2. Builds a Quarkus native binary (`-Dnative` with container build)
3. Packages it with [`src/main/docker/Dockerfile.native`](src/main/docker/Dockerfile.native)
4. Pushes to `quay.io/mklaasse/frida-carclaims-backend-ai`

### Required GitHub secrets

| Secret | Value |
|--------|-------|
| `QUAY_USERNAME` | Your Quay.io username (`mklaasse`) or robot account name |
| `QUAY_PASSWORD` | Quay.io password or robot account token |

Create the repository `frida-carclaims-backend-ai` under your Quay.io account before the first push.

Pull the image locally:

```shell
docker pull quay.io/mklaasse/frida-carclaims-backend-ai:latest
docker run --rm -p 8080:8080 \
  -e LITELLM_BASE_URL=https://litellm-litemaas.apps.prod.rhoai.rh-aiservices-bu.com/v1/ \
  -e LITELLM_API_KEY=sk-... \
  -e TRANSCRIPTION_BASE_URL=https://api.openai.com/v1/ \
  -e TRANSCRIPTION_API_KEY=sk-proj-... \
  quay.io/mklaasse/frida-carclaims-backend-ai:latest
```

## Deployment

This repository contains the application code only. Kubernetes deployment manifests (Helm charts, ArgoCD applications) are maintained in the separate [frida-carclaims-ai-gitops](https://github.com/sa-mw-dach/frida-carclaims-ai-gitops) repository.

See [TRANSCRIPTION_MIGRATION.md](TRANSCRIPTION_MIGRATION.md) for migration details from self-hosted Whisper to OpenAI API.

## Security

Do not commit API keys. Use environment variables or a local `.env` file. Files matching `*.secret` are gitignored.
