# Voice backend (test)

Quarkus native image from `quay.io/mklaasse/frida-carclaims-backend-ai`.

## Prerequisites

- Namespace `frida-carclaims-test` exists (created by `gitops/test/whisper/`)
- Whisper service is running in the same namespace
- LiteMaas / LiteLLM credentials available

## Create secrets

```bash
oc create secret generic voice-backend-secrets -n frida-carclaims-test \
  --from-literal=LITELLM_API_KEY='sk-...'
```

Add `WHISPER_API_KEY` only if your Whisper endpoint requires authentication:

```bash
oc create secret generic voice-backend-secrets -n frida-carclaims-test \
  --from-literal=LITELLM_API_KEY='sk-...' \
  --from-literal=WHISPER_API_KEY='...'
```

See [`secret.yaml.example`](secret.yaml.example) for the Secret shape.

## Configuration

Non-secret settings are in [`configmap.yaml`](configmap.yaml). Adjust `LITELLM_BASE_URL` for your LiteMaas environment before applying.

`WHISPER_BASE_URL` points at the in-cluster Whisper service:

`http://whisper.frida-carclaims-test.svc.cluster.local:8000/inference`

## Apply

```bash
oc apply -k gitops/test/backend/
```

Or deploy everything (Whisper + backend):

```bash
oc apply -k gitops/test/
```

## API

After deploy, the OpenShift Route exposes:

`POST /api/voice/extract` (multipart: `audio`, optional `currentState`, optional `language`)

```bash
oc get route voice-backend -n frida-carclaims-test -o jsonpath='https://{.spec.host}{"\n"}'
```
