# Audio Transcription Migration Guide

## Overview

The backend now supports **OpenAI-compatible audio transcription APIs** in addition to the legacy whisper.cpp implementation. This allows you to use any OpenAI-compatible speech-to-text service instead of running Whisper in-cluster.

## Architecture Changes

### Before
- **Speech-to-Text**: In-cluster whisper.cpp container
- **Data Extraction**: External LLM via LiteLLM (Qwen3.6-35B-A3B)

### After
- **Speech-to-Text**: OpenAI-compatible API (external)
- **Data Extraction**: External LLM via LiteLLM (unchanged)

## Configuration

### Environment Variables

The backend uses these environment variables for audio transcription:

| Variable | Description | Default | Example |
|----------|-------------|---------|---------|
| `TRANSCRIPTION_BASE_URL` | Base URL for the OpenAI-compatible transcription API | `https://api.openai.com/v1/` | `https://api.openai.com/v1/` |
| `TRANSCRIPTION_API_KEY` | API key for authentication | _(required)_ | `sk-...` |
| `TRANSCRIPTION_MODEL` | Model name | `whisper-1` | `whisper-1` |

## Deployment Options

### Option 1: OpenAI API (Recommended)

Use OpenAI's hosted Whisper API:

```yaml
backend:
  config:
    transcriptionBaseUrl: "https://api.openai.com/v1/"
    transcriptionModel: "whisper-1"
```

Create a secret with your OpenAI API key:

```bash
oc create secret generic voice-backend-secrets \
  --from-literal=LITELLM_API_KEY='sk-...' \
  --from-literal=TRANSCRIPTION_API_KEY='sk-...' \
  -n frida-carclaims-dev
```

### Option 2: Self-Hosted OpenAI-Compatible Service

Use any OpenAI-compatible speech-to-text service:

```yaml
backend:
  config:
    transcriptionBaseUrl: "https://your-whisper-api.example.com/v1/"
    transcriptionModel: "whisper-1"
```

## Available Models

**Important**: None of the models you currently have access to support audio transcription. You'll need to:

1. Use OpenAI's Whisper API (`https://api.openai.com/v1/audio/transcriptions`)
2. Deploy your own OpenAI-compatible Whisper service
3. Request access to a speech-to-text model in your cluster

Your available models support:
- **Chat**: granite, llama, qwen, deepseek, phi, etc.
- **Embeddings**: nomic-embed-text-v1-5
- **No audio transcription models available**

## Migration Steps

### 1. Update Backend Code

The code changes are already committed:
- New `OpenAiAudioTranscriptionModel` class
- Updated `AudioTranscriptionModelProducer` to support both providers
- Updated configuration in `application.properties`

### 2. Update GitOps Configuration

The Whisper deployment has been completely removed. Your environment values should now look like:

```yaml
# environments/dev/values.yaml
backend:
  config:
    transcriptionBaseUrl: "https://api.openai.com/v1/"
    transcriptionModel: "whisper-1"
```

### 3. Create/Update Secrets

Add the transcription API key to your secrets:

```bash
# Development
oc create secret generic voice-backend-secrets \
  --from-literal=LITELLM_API_KEY='your-litellm-key' \
  --from-literal=TRANSCRIPTION_API_KEY='your-openai-key' \
  --dry-run=client -o yaml | oc apply -f - -n frida-carclaims-dev

# Stage
oc create secret generic voice-backend-secrets \
  --from-literal=LITELLM_API_KEY='your-litellm-key' \
  --from-literal=TRANSCRIPTION_API_KEY='your-openai-key' \
  --dry-run=client -o yaml | oc apply -f - -n frida-carclaims-stage

# Production
oc create secret generic voice-backend-secrets \
  --from-literal=LITELLM_API_KEY='your-litellm-key' \
  --from-literal=TRANSCRIPTION_API_KEY='your-openai-key' \
  --dry-run=client -o yaml | oc apply -f - -n frida-carclaims-prod
```

### 4. Build and Deploy

Build a new container image with the updated code:

```bash
cd frida-carclaims-voice-backend
./mvnw clean package -Dquarkus.container-image.build=true
docker tag ... quay.io/mklaassen/frida-carclaims-backend-ai:v2.0.0
docker push quay.io/mklaassen/frida-carclaims-backend-ai:v2.0.0
```

Update the image tag in your GitOps repository and commit:

```bash
cd frida-carclaims-ai-gitops
# Edit environments/dev/values.yaml to use the new tag
git add .
git commit -m "Migrate to OpenAI-compatible audio transcription"
git push
```

## Testing

Test the transcription endpoint:

```bash
# Record audio and convert to base64
AUDIO_BASE64=$(base64 < audio.webm)

# Call the API
curl -X POST https://frida-carclaims-dev.<your-domain>/api/extract \
  -H "Content-Type: application/json" \
  -d '{
    "audioData": "'$AUDIO_BASE64'",
    "mimeType": "audio/webm",
    "currentState": "{}",
    "language": "de"
  }'
```

## Troubleshooting

### Error: "Audio transcription is not configured"

Ensure `TRANSCRIPTION_BASE_URL` and `TRANSCRIPTION_API_KEY` are set in your environment.

### Error: "OpenAI transcription API returned HTTP 401"

Check that your `TRANSCRIPTION_API_KEY` is valid.

### Error: "Audio transcription failed"

Check the logs for details:

```bash
oc logs -n frida-carclaims-dev deployment/voice-backend
```

## Benefits

1. **No in-cluster Whisper**: Reduced resource usage and complexity
2. **Better quality**: OpenAI's Whisper API is highly optimized
3. **Flexibility**: Easy to switch between providers
4. **Cost optimization**: Pay only for what you use vs. always-running containers
