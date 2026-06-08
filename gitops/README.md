# GitOps (test only)

This `gitops/` folder lives **in the application repository** on purpose: the stack is currently deployed only to the **test** environment and kept simple for iteration.

## Scope

| Component | Deployment approach |
|-----------|---------------------|
| Backend (this app) | Built by GitHub Actions → `quay.io/mklaasse/frida-carclaims-backend-ai` |
| LiteLLM / Qwen (LiteMaas) | External managed service — no manifests here |
| Whisper (ramalama) | Manifests under `gitops/test/whisper/` |

## Production

For **production**, do **not** keep cluster manifests in this app repo. Use a **separate Helm/GitOps repository** (e.g. Argo CD / Flux) with environment-specific values (`test`, `staging`, `prod`).

## Test deployment

```bash
# 1. Deploy Whisper (namespace, PVC, Deployment, Service, Route)
kubectl apply -k gitops/test/whisper/
# or on OpenShift:
oc apply -k gitops/test/whisper/

# 2. Load the Whisper model into the PVC (once) — see gitops/test/whisper/README.md

# 3. Deploy the backend separately with runtime env vars:
#    LITELLM_BASE_URL, LITELLM_API_KEY  — LiteMaas service
#    WHISPER_BASE_URL=http://whisper.frida-carclaims-test.svc.cluster.local:8000/inference
#    WHISPER_API_KEY                    — only if your Whisper gateway requires auth
```

Verify the inference endpoint after deploy:

```bash
kubectl -n frida-carclaims-test port-forward svc/whisper 8000:8000
curl -sS -X POST "http://localhost:8000/inference" ...
```
