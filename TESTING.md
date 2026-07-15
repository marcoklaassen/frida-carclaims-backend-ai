# Local Testing Guide

This guide helps you test the OpenAI audio transcription migration locally using Quarkus tests before deploying to production.

## Prerequisites

1. **Java 21** installed
2. **Maven** (wrapper included)
3. **OpenAI API key** (or compatible service) - optional for unit tests, required for integration tests

## Test Types

### Unit Tests

Run without any API keys - test the code logic:

```bash
./mvnw test
```

This runs:
- ✅ `OpenAiAudioTranscriptionModelTest` - Tests response parsing and validation
- ✅ `AudioTranscriptionModelProducerTest` - Tests CDI bean creation
- ✅ All other existing unit tests

### Integration Tests

Run against real OpenAI API:

```bash
# Set your API key
export TRANSCRIPTION_API_KEY="sk-your-openai-key"
export TRANSCRIPTION_BASE_URL="https://api.openai.com/v1/"  # optional

# Run specific integration test
./mvnw test -Dtest=OpenAiTranscriptionIntegrationTest
```

This test:
- ✅ Creates a minimal valid WAV audio file
- ✅ Calls the real OpenAI API
- ✅ Verifies the response structure
- ✅ Tests both English and German language support
- ⚠️ **Skipped automatically** if `TRANSCRIPTION_API_KEY` is not set

## Running the Full Application in Dev Mode

### 1. Set Environment Variables

```bash
export TRANSCRIPTION_BASE_URL="https://api.openai.com/v1/"
export TRANSCRIPTION_API_KEY="sk-your-openai-key"
export TRANSCRIPTION_MODEL="whisper-1"

export CHAT_BASE_URL="https://litellm-litemaas.apps.prod.rhoai.rh-aiservices-bu.com/v1/"
export CHAT_API_KEY="sk-your-chat-key"
export CHAT_MODEL="Qwen3.6-35B-A3B"
```

### 2. Start Quarkus Dev Mode

```bash
./mvnw quarkus:dev
```

Dev mode features:
- 🔄 Hot reload on code changes
- 🐛 Debugger on port 5005
- 📊 Dev UI at http://localhost:8080/q/dev
- 🏥 Health check at http://localhost:8080/q/health

### 3. Test the API Manually

Use cURL or the Quarkus Dev UI:

```bash
# Test the endpoint with real audio
curl -X POST http://localhost:8080/api/extract \
  -H "Content-Type: application/json" \
  -d '{
    "audioData": "'"$(base64 -i your-audio.wav | tr -d '\n')"'",
    "mimeType": "audio/wav",
    "currentState": "{}",
    "language": "de"
  }' | jq '.'
```

Expected response:

```json
{
  "transcript": "...",
  "extractedData": {
    "accidentDate": "...",
    ...
  }
}
```

## Test Coverage

### What's Tested

✅ **Unit Tests**
- OpenAI response parsing (JSON and plain text)
- Configuration validation (requires base URL and API key)
- Bean injection and creation
- Error handling

✅ **Integration Tests** (with API key)
- Real OpenAI API calls
- Audio transcription end-to-end
- Multiple language support (English, German)

✅ **Existing Tests** (still work)
- Voice extraction service
- Claims data merging
- JSON parsing
- Schema knowledge

### Running All Tests

```bash
# Unit tests only (no API key needed)
./mvnw test

# All tests including integration (requires API key)
export TRANSCRIPTION_API_KEY="sk-your-key"
./mvnw verify
```

## Continuous Integration

For CI/CD pipelines:

```yaml
# .github/workflows/test.yml or similar
env:
  TRANSCRIPTION_API_KEY: ${{ secrets.OPENAI_API_KEY }}
  TRANSCRIPTION_BASE_URL: "https://api.openai.com/v1/"

steps:
  - name: Run unit tests
    run: ./mvnw test

  - name: Run integration tests
    run: ./mvnw test -Dtest=OpenAiTranscriptionIntegrationTest
    if: env.TRANSCRIPTION_API_KEY != ''
```

Integration tests are automatically skipped in CI if the API key is not configured.

## Troubleshooting

### Unit Test Failures

**Issue**: Tests fail with compilation errors

**Solution**: Ensure you're using Java 21:
```bash
java -version  # Should show 21.x
```

### Integration Test Failures

**Issue**: `OpenAiTranscriptionIntegrationTest` fails with 401

**Cause**: Invalid API key

**Solution**: Verify your key works:
```bash
curl https://api.openai.com/v1/models \
  -H "Authorization: Bearer $TRANSCRIPTION_API_KEY"
```

**Issue**: Integration test fails with timeout

**Cause**: Network issues or API rate limits

**Solution**: 
- Check internet connectivity
- Wait a few minutes (rate limit)
- Verify OpenAI API status

### Dev Mode Issues

**Issue**: Backend fails to start with "Audio transcription is not configured"

**Solution**: Set the required environment variables:
```bash
export TRANSCRIPTION_BASE_URL="https://api.openai.com/v1/"
export TRANSCRIPTION_API_KEY="sk-your-key"
```

**Issue**: OpenAI API returns 429 (rate limit)

**Solution**: 
- Wait and retry
- Check your OpenAI usage quota
- Consider upgrading your plan

## What Changed from Whisper.cpp

| Aspect | Before | After |
|--------|--------|-------|
| **Implementation** | `WhisperCppTranscriptionModel` | `OpenAiAudioTranscriptionModel` |
| **Test file** | `WhisperCppTranscriptionModelTest` (removed) | `OpenAiAudioTranscriptionModelTest` |
| **Integration test** | None | `OpenAiTranscriptionIntegrationTest` |
| **API endpoint** | `/inference` (whisper.cpp) | `/v1/audio/transcriptions` (OpenAI) |
| **Configuration** | `WHISPER_BASE_URL`, `WHISPER_API_KEY` | `TRANSCRIPTION_BASE_URL`, `TRANSCRIPTION_API_KEY` |

## Next Steps

Once tests pass locally:

1. ✅ **Run all unit tests**: `./mvnw test`
2. ✅ **Run integration tests**: `./mvnw test -Dtest=OpenAiTranscriptionIntegrationTest`
3. ✅ **Test in dev mode**: `./mvnw quarkus:dev`
4. ✅ **Build container**: `./mvnw clean package -Dquarkus.container-image.build=true`
5. ✅ **Deploy to cluster**: Update GitOps and push

See [TRANSCRIPTION_MIGRATION.md](./TRANSCRIPTION_MIGRATION.md) for deployment steps.
