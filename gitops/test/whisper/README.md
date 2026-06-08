# Whisper (test)

Kubernetes/OpenShift manifests for [ramalama-whisper-server](https://quay.io/repository/ramalama/ramalama-whisper-server), adapted from a local Podman Desktop pod spec.

## Model file

An **init container** downloads `ggml-small.bin` (~466 MB) from Hugging Face into the PVC on first start:

`https://huggingface.co/ggerganov/whisper.cpp/resolve/main/ggml-small.bin`

The PVC is mounted at `/models`; the Whisper container reads `/models/ggml-small.bin`.

### Troubleshooting `invalid model data (bad magic)`

This error means the model file is missing, empty, or not a valid whisper.cpp binary. Common causes:

- PVC was empty and the old `subPath` file mount created an empty directory instead of a file
- Manual `oc cp` was interrupted or targeted the wrong path
- PVC already contains a corrupt file from a failed first deploy

**Fix — wipe and redeploy:**

```bash
oc scale deployment whisper -n frida-carclaims-test --replicas=0
oc delete pvc whisper-model -n frida-carclaims-test
oc apply -k gitops/test/whisper/
```

The init container re-downloads the model on the next pod start.

**Verify the file inside the PVC:**

```bash
oc logs -n frida-carclaims-test deployment/whisper -c download-model
oc exec -n frida-carclaims-test deployment/whisper -c whisper -- ls -la /models/
```

Expect `ggml-small.bin` with roughly 466000000 bytes.

### Manual copy (alternative)

If the cluster cannot reach Hugging Face, copy from your local Podman Desktop model path:

```bash
oc project frida-carclaims-test

oc run whisper-model-copy --restart=Never \
  --image=registry.access.redhat.com/ubi9/ubi-minimal:9.7 \
  --overrides='{"spec":{"containers":[{"name":"whisper-model-copy","image":"registry.access.redhat.com/ubi9/ubi-minimal:9.7","command":["sleep","3600"],"volumeMounts":[{"name":"m","mountPath":"/models"}]}],"volumes":[{"name":"m","persistentVolumeClaim":{"claimName":"whisper-model"}}]}}'

oc cp ~/.local/share/containers/podman-desktop/extensions-storage/redhat.ai-lab/models/hf.ggerganov.whisper.cpp/ggml-small.bin \
  whisper-model-copy:/models/ggml-small.bin

oc delete pod whisper-model-copy
oc rollout restart deployment/whisper -n frida-carclaims-test
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
