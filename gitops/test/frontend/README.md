# Frontend (test)

React app from `quay.io/mklaasse/frida-carclaims-frontend`, served by nginx.

## Same-host API routing

Public Route: **https://frida-carclaims-test.apps.ocp4.klaassen.click/**

Nginx proxies `/api/` to the backend Service:

```
VOICE_API_BACKEND=http://frida-carclaims-test:8080
```

```
Browser  →  https://frida-carclaims-test.apps.ocp4.klaassen.click/
              ├── /                    → React SPA
              └── /api/voice/extract   → http://frida-carclaims-test:8080/api/voice/extract
```

The frontend calls `/api/voice/extract` as a relative URL. No backend Route is required.

## Apply

```bash
oc apply -k gitops/test/frontend/
```

Requires the backend Service `frida-carclaims-test` in the same namespace.
