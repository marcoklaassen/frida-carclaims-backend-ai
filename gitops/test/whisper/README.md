# Whisper (test)

Kubernetes/OpenShift manifests for [ramalama-whisper-server](https://quay.io/repository/ramalama/ramalama-whisper-server), adapted from a local Podman Desktop pod spec.

## Model file

The deployment expects `ggml-small.bin` on the PVC at path `ggml-small.bin` (mounted as `/models/ggml-small.bin` in the container).

### One-time copy (OpenShift example)

```bash
oc project frida-carclaims-test

oc run whisper-model-copy --restart=Never \
  --image=registry.access.redhat.com/ubi9/ubi-minimal:9.7 \
  --overrides='{"spec":{"containers":[{"name":"whisper-model-copy","image":"registry.access.redhat.com/ubi9/ubi-minimal:9.7","command":["sleep","3600"],"volumeMounts":[{"name":"m","mountPath":"/models"}]}],"volumes":[{"name":"m","persistentVolumeClaim":{"claimName":"whisper-model"}}]}}'

# Adjust the local source path to your machine
oc cp ~/.local/share/containers/podman-desktop/extensions-storage/redhat.ai-lab/models/hf.ggerganov.whisper.cpp/ggml-small.bin \
  whisper-model-copy:/models/ggml-small.bin

oc delete pod whisper-model-copy
```

## Backend connection

Set on the voice-backend deployment:

| Variable | Value |
|----------|-------|
| `WHISPER_BASE_URL` | `http://whisper.frida-carclaims-test.svc.cluster.local:8000/inference` |
| `WHISPER_API_KEY` | Omit unless the Whisper endpoint requires authentication |

## Apply

```bash
kubectl apply -k gitops/test/whisper/
```
