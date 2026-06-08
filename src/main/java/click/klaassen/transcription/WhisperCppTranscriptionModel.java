package click.klaassen.transcription;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import dev.langchain4j.data.audio.Audio;
import dev.langchain4j.model.audio.AudioTranscriptionModel;
import dev.langchain4j.model.audio.AudioTranscriptionRequest;
import dev.langchain4j.model.audio.AudioTranscriptionResponse;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.Base64;
import java.util.Optional;
import java.util.UUID;

/**
 * Transcription client for whisper.cpp server ({@code POST /inference}).
 * Not compatible with OpenAI {@code /v1/audio/transcriptions}.
 */
public class WhisperCppTranscriptionModel implements AudioTranscriptionModel {

    private static final Duration TIMEOUT = Duration.ofMinutes(5);

    private final String inferenceUrl;
    private final Optional<String> apiKey;
    private final HttpClient httpClient;
    private final ObjectMapper objectMapper;

    public WhisperCppTranscriptionModel(String inferenceUrl, Optional<String> apiKey, ObjectMapper objectMapper) {
        this.inferenceUrl = inferenceUrl;
        this.apiKey = apiKey;
        this.objectMapper = objectMapper;
        this.httpClient = HttpClient.newBuilder()
                .connectTimeout(TIMEOUT)
                .build();
    }

    @Override
    public AudioTranscriptionResponse transcribe(AudioTranscriptionRequest request) {
        try {
            byte[] audioBytes = toAudioBytes(request.audio());
            String filename = filenameForMimeType(request.audio().mimeType());
            byte[] body = buildMultipartBody(audioBytes, filename, request.language());

            HttpRequest.Builder httpRequestBuilder = HttpRequest.newBuilder()
                    .uri(URI.create(inferenceUrl))
                    .timeout(TIMEOUT)
                    .header("Content-Type", "multipart/form-data; boundary=" + BOUNDARY)
                    .POST(HttpRequest.BodyPublishers.ofByteArray(body));

            apiKey.filter(key -> !key.isBlank())
                    .ifPresent(key -> httpRequestBuilder.header("Authorization", "Bearer " + key));

            HttpResponse<String> response = httpClient.send(
                    httpRequestBuilder.build(),
                    HttpResponse.BodyHandlers.ofString());

            if (response.statusCode() < 200 || response.statusCode() >= 300) {
                throw new IOException("Whisper.cpp returned HTTP " + response.statusCode() + ": " + response.body());
            }

            return new AudioTranscriptionResponse(parseTranscript(response.body()));
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new RuntimeException("Whisper.cpp transcription interrupted", e);
        } catch (IOException e) {
            throw new RuntimeException("Whisper.cpp transcription failed", e);
        }
    }

    String parseTranscript(String body) throws IOException {
        String trimmed = body.trim();
        if (trimmed.startsWith("{")) {
            JsonNode json = objectMapper.readTree(trimmed);
            JsonNode textNode = json.get("text");
            if (textNode != null && !textNode.isNull()) {
                return textNode.asText().trim();
            }
        }
        return trimmed;
    }

    private byte[] toAudioBytes(Audio audio) throws IOException {
        if (audio.binaryData() != null && audio.binaryData().length > 0) {
            return audio.binaryData();
        }
        if (audio.base64Data() != null && !audio.base64Data().isBlank()) {
            return Base64.getDecoder().decode(audio.base64Data());
        }
        if (audio.url() != null) {
            try {
                HttpResponse<byte[]> response = httpClient.send(
                        HttpRequest.newBuilder(audio.url()).GET().build(),
                        HttpResponse.BodyHandlers.ofByteArray());
                if (response.statusCode() < 200 || response.statusCode() >= 300) {
                    throw new IOException("Failed to fetch audio from URL: HTTP " + response.statusCode());
                }
                return response.body();
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                throw new IOException("Interrupted while fetching audio from URL", e);
            }
        }
        throw new IOException("Audio has no binaryData, base64Data, or url");
    }

    private static final String BOUNDARY = "----WhisperCpp" + UUID.randomUUID();

    private byte[] buildMultipartBody(byte[] audioBytes, String filename, String language) throws IOException {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        writeField(out, "file", audioBytes, filename, "application/octet-stream");
        writeField(out, "response_format", "json");
        writeField(out, "temperature", "0.0");
        writeField(out, "translate", "false");
        if (language != null && !language.isBlank()) {
            writeField(out, "language", language);
        }
        out.write(("--" + BOUNDARY + "--\r\n").getBytes(StandardCharsets.UTF_8));
        return out.toByteArray();
    }

    private void writeField(ByteArrayOutputStream out, String name, String value) throws IOException {
        out.write(("--" + BOUNDARY + "\r\n").getBytes(StandardCharsets.UTF_8));
        out.write(("Content-Disposition: form-data; name=\"" + name + "\"\r\n\r\n").getBytes(StandardCharsets.UTF_8));
        out.write(value.getBytes(StandardCharsets.UTF_8));
        out.write("\r\n".getBytes(StandardCharsets.UTF_8));
    }

    private void writeField(ByteArrayOutputStream out, String name, byte[] content, String filename, String contentType)
            throws IOException {
        out.write(("--" + BOUNDARY + "\r\n").getBytes(StandardCharsets.UTF_8));
        out.write(("Content-Disposition: form-data; name=\"" + name + "\"; filename=\"" + filename + "\"\r\n")
                .getBytes(StandardCharsets.UTF_8));
        out.write(("Content-Type: " + contentType + "\r\n\r\n").getBytes(StandardCharsets.UTF_8));
        out.write(content);
        out.write("\r\n".getBytes(StandardCharsets.UTF_8));
    }

    private String filenameForMimeType(String mimeType) {
        if (mimeType == null) {
            return "audio.webm";
        }
        return switch (mimeType.toLowerCase()) {
            case "audio/wav", "audio/x-wav", "audio/wave" -> "audio.wav";
            case "audio/mpeg", "audio/mp3" -> "audio.mp3";
            case "audio/ogg" -> "audio.ogg";
            case "audio/mp4", "audio/m4a", "audio/x-m4a" -> "audio.m4a";
            default -> "audio.webm";
        };
    }
}
