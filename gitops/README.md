# GitOps (test only)

This `gitops/` folder lives **in the application repository** on purpose: the stack is currently deployed only to the **test** environment and kept simple for iteration.

## Scope

| Component | Deployment approach |
|-----------|---------------------|
| Frontend | `gitops/test/frontend/` → public Route |
| Backend | `gitops/test/backend/` → Service `frida-carclaims-test:8080`, no Route |
| LiteLLM / Qwen (LiteMaas) | External — ConfigMap + Secret |
| Whisper | `gitops/test/whisper/` — ClusterIP only |

## Public URL

**https://frida-carclaims-test.apps.ocp4.klaassen.click/**

- `/` — React frontend
- `/api/voice/extract` — nginx proxies to `http://frida-carclaims-test:8080`

## Production

For **production**, use a **separate Helm/GitOps repository**.

## Test deployment

```bash
oc create secret generic voice-backend-secrets -n frida-carclaims-test \
  --from-literal=LITELLM_API_KEY='sk-...'

oc apply -k gitops/test/

# Remove legacy backend Route if present
oc delete route voice-backend -n frida-carclaims-test --ignore-not-found
```

## Layout

```
gitops/test/
├── kustomization.yaml
├── whisper/
├── backend/     # Service name: frida-carclaims-test
└── frontend/
```

## Verify

```bash
curl -sS -o /dev/null -w "%{http_code}\n" "https://frida-carclaims-test.apps.ocp4.klaassen.click/"
```
