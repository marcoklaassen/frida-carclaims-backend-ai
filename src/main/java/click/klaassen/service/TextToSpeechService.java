package click.klaassen.service;

import click.klaassen.exception.UpstreamAiException;
import jakarta.enterprise.context.ApplicationScoped;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.logging.Logger;
import org.eclipse.microprofile.config.inject.ConfigProperty;

@ApplicationScoped
public class TextToSpeechService {

    private static final Logger LOG = Logger.getLogger(TextToSpeechService.class.getName());

    @ConfigProperty(name = "assistant.tts.base-url", defaultValue = "https://api.openai.com/v1")
    String baseUrl;

    @ConfigProperty(name = "assistant.tts.api-key")
    String apiKey;

    @ConfigProperty(name = "assistant.tts.model", defaultValue = "tts-1")
    String model;

    @ConfigProperty(name = "assistant.tts.voice", defaultValue = "alloy")
    String voice;

    private final HttpClient httpClient = HttpClient.newHttpClient();

    public byte[] generateSpeech(String text) {
        String body = """
                {"model":"%s","voice":"%s","input":"%s","response_format":"mp3"}"""
                .formatted(model, voice, escapeJson(text));

        String url = baseUrl.endsWith("/") ? baseUrl + "audio/speech" : baseUrl + "/audio/speech";

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .header("Authorization", "Bearer " + apiKey)
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(body))
                .build();

        try {
            HttpResponse<byte[]> response = httpClient.send(request, HttpResponse.BodyHandlers.ofByteArray());
            if (response.statusCode() != 200) {
                throw new UpstreamAiException("TTS API returned status " + response.statusCode()
                        + ": " + new String(response.body()), null);
            }
            LOG.info("TTS generated: " + response.body().length + " bytes for text length " + text.length());
            return response.body();
        } catch (UpstreamAiException e) {
            throw e;
        } catch (Exception e) {
            throw new UpstreamAiException("TTS API call failed", e);
        }
    }

    static String escapeJson(String text) {
        return text.replace("\\", "\\\\")
                .replace("\"", "\\\"")
                .replace("\n", "\\n")
                .replace("\r", "\\r")
                .replace("\t", "\\t");
    }
}
