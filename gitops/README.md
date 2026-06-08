# GitOps (test only)

This `gitops/` folder lives **in the application repository** on purpose: the stack is currently deployed only to the **test** environment and kept simple for iteration.

## Scope

| Component | Deployment approach |
|-----------|---------------------|
| Backend (this app) | `gitops/test/backend/` → `quay.io/mklaasse/frida-carclaims-backend-ai` |
| LiteLLM / Qwen (LiteMaas) | External managed service — URL/key via ConfigMap + Secret |
| Whisper (ramalama) | `gitops/test/whisper/` — ClusterIP only, no public Route |

## Production

For **production**, do **not** keep cluster manifests in this app repo. Use a **separate Helm/GitOps repository** (e.g. Argo CD / Flux) with environment-specific values (`test`, `staging`, `prod`).

## Test deployment

```bash
# 1. Create backend secrets (once)
oc create secret generic voice-backend-secrets -n frida-carclaims-test \
  --from-literal=LITELLM_API_KEY='sk-...'

# 2. Deploy Whisper + backend (or apply components separately)
oc apply -k gitops/test/

# 3. Wait for Whisper init container to download the model (~466 MB) — see gitops/test/whisper/README.md
```

Deploy components individually:

```bash
oc apply -k gitops/test/whisper/
oc apply -k gitops/test/backend/   # requires voice-backend-secrets
```

## Layout

```
gitops/test/
├── kustomization.yaml    # whisper + backend
├── whisper/
└── backend/
```

## Verify

```bash
# Backend API (public Route)
oc get route voice-backend -n frida-carclaims-test
curl -sS "https://<route-host>/q/openapi"

# Whisper (cluster-internal — debug only)
kubectl -n frida-carclaims-test port-forward svc/whisper 8000:8000
```
