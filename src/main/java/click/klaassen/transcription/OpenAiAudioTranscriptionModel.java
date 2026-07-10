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
 * OpenAI-compatible audio transcription client for {@code POST /v1/audio/transcriptions}.
 * Compatible with OpenAI Whisper API and any OpenAI-compatible speech-to-text service.
 */
public class OpenAiAudioTranscriptionModel implements AudioTranscriptionModel {

    private static final Duration TIMEOUT = Duration.ofMinutes(5);
    private static final String DEFAULT_MODEL = "whisper-1";

    private final String baseUrl;
    private final String apiKey;
    private final String model;
    private final HttpClient httpClient;
    private final ObjectMapper objectMapper;

    public OpenAiAudioTranscriptionModel(
            String baseUrl,
            String apiKey,
            String model,
            ObjectMapper objectMapper) {
        if (baseUrl == null || baseUrl.isBlank()) {
            throw new IllegalArgumentException("baseUrl is required");
        }
        if (apiKey == null || apiKey.isBlank()) {
            throw new IllegalArgumentException("apiKey is required");
        }
        this.baseUrl = baseUrl.endsWith("/") ? baseUrl : baseUrl + "/";
        this.apiKey = apiKey;
        this.model = model != null && !model.isBlank() ? model : DEFAULT_MODEL;
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

            String url = baseUrl + "audio/transcriptions";
            HttpRequest httpRequest = HttpRequest.newBuilder()
                    .uri(URI.create(url))
                    .timeout(TIMEOUT)
                    .header("Content-Type", "multipart/form-data; boundary=" + BOUNDARY)
                    .header("Authorization", "Bearer " + apiKey)
                    .POST(HttpRequest.BodyPublishers.ofByteArray(body))
                    .build();

            HttpResponse<String> response = httpClient.send(
                    httpRequest,
                    HttpResponse.BodyHandlers.ofString());

            if (response.statusCode() < 200 || response.statusCode() >= 300) {
                throw new IOException("OpenAI transcription API returned HTTP "
                        + response.statusCode() + ": " + response.body());
            }

            return new AudioTranscriptionResponse(parseTranscript(response.body()));
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new RuntimeException("Audio transcription interrupted", e);
        } catch (IOException e) {
            throw new RuntimeException("Audio transcription failed", e);
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

    private static final String BOUNDARY = "----OpenAiAudioBoundary" + UUID.randomUUID();

    private byte[] buildMultipartBody(byte[] audioBytes, String filename, String language) throws IOException {
        ByteArrayOutputStream out = new ByteArrayOutputStream();

        // file field (required)
        writeField(out, "file", audioBytes, filename, "application/octet-stream");

        // model field (required)
        writeField(out, "model", model);

        // response_format (optional, default is json)
        writeField(out, "response_format", "json");

        // language (optional)
        if (language != null && !language.isBlank()) {
            writeField(out, "language", language);
        }

        // temperature (optional, 0-1, default 0)
        writeField(out, "temperature", "0.0");

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
            case "audio/flac" -> "audio.flac";
            default -> "audio.webm";
        };
    }
}
