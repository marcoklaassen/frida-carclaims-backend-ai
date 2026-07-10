package click.klaassen.integration;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assumptions.assumeTrue;

import click.klaassen.transcription.OpenAiAudioTranscriptionModel;
import com.fasterxml.jackson.databind.ObjectMapper;
import dev.langchain4j.data.audio.Audio;
import dev.langchain4j.model.audio.AudioTranscriptionRequest;
import dev.langchain4j.model.audio.AudioTranscriptionResponse;
import java.util.Base64;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/**
 * Integration test for OpenAI audio transcription.
 *
 * To run this test with real OpenAI API:
 *
 * <pre>
 * export TRANSCRIPTION_API_KEY="sk-your-openai-key"
 * export TRANSCRIPTION_BASE_URL="https://api.openai.com/v1/"  # optional
 * mvn test -Dtest=OpenAiTranscriptionIntegrationTest
 * </pre>
 *
 * This test is SKIPPED if TRANSCRIPTION_API_KEY is not set.
 */
class OpenAiTranscriptionIntegrationTest {

    private String apiKey;
    private String baseUrl;
    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        apiKey = System.getenv("TRANSCRIPTION_API_KEY");
        baseUrl = System.getenv("TRANSCRIPTION_BASE_URL");
        if (baseUrl == null || baseUrl.isBlank()) {
            baseUrl = "https://api.openai.com/v1/";
        }
        objectMapper = new ObjectMapper();
    }

    @Test
    void testOpenAiTranscription() {
        // Skip test if API key is not configured
        assumeTrue(apiKey != null && !apiKey.isBlank(),
                "Skipping integration test - TRANSCRIPTION_API_KEY not set. " +
                        "Set it to run this test against real OpenAI API.");

        // Create a minimal audio sample (8kHz mono WAV header + 1 second of silence)
        // This is a valid WAV file that OpenAI Whisper can process
        byte[] wavAudio = createMinimalWavAudio();

        OpenAiAudioTranscriptionModel model = new OpenAiAudioTranscriptionModel(
                baseUrl, apiKey, "whisper-1", objectMapper);

        Audio audio = Audio.builder()
                .binaryData(wavAudio)
                .mimeType("audio/wav")
                .build();

        AudioTranscriptionRequest request = AudioTranscriptionRequest.builder(audio)
                .language("en")
                .build();

        AudioTranscriptionResponse response = model.transcribe(request);

        assertNotNull(response, "Response should not be null");
        assertNotNull(response.text(), "Transcript should not be null");

        // Even for silence, Whisper might return empty string or whitespace
        // The important thing is that the API call succeeded
        System.out.println("✅ OpenAI transcription successful");
        System.out.println("   API: " + baseUrl);
        System.out.println("   Transcript: '" + response.text() + "'");
    }

    @Test
    void testOpenAiTranscriptionWithGermanAudio() {
        assumeTrue(apiKey != null && !apiKey.isBlank(),
                "Skipping integration test - TRANSCRIPTION_API_KEY not set");

        // Note: For real testing, you would provide actual German audio
        // This test demonstrates the API structure
        byte[] wavAudio = createMinimalWavAudio();

        OpenAiAudioTranscriptionModel model = new OpenAiAudioTranscriptionModel(
                baseUrl, apiKey, "whisper-1", objectMapper);

        Audio audio = Audio.builder()
                .binaryData(wavAudio)
                .mimeType("audio/wav")
                .build();

        AudioTranscriptionRequest request = AudioTranscriptionRequest.builder(audio)
                .language("de") // German
                .build();

        AudioTranscriptionResponse response = model.transcribe(request);

        assertNotNull(response);
        assertNotNull(response.text());

        System.out.println("✅ German audio transcription successful");
        System.out.println("   Transcript: '" + response.text() + "'");
    }

    /**
     * Creates a minimal valid WAV file (8kHz mono, 1 second of silence).
     * This is the smallest valid audio file that OpenAI Whisper will accept.
     */
    private byte[] createMinimalWavAudio() {
        int sampleRate = 8000;
        int duration = 1; // 1 second
        int numSamples = sampleRate * duration;

        // WAV header (44 bytes) + PCM data
        byte[] wav = new byte[44 + numSamples * 2];

        // RIFF header
        wav[0] = 'R';
        wav[1] = 'I';
        wav[2] = 'F';
        wav[3] = 'F';
        writeInt(wav, 4, 36 + numSamples * 2); // File size - 8
        wav[8] = 'W';
        wav[9] = 'A';
        wav[10] = 'V';
        wav[11] = 'E';

        // fmt chunk
        wav[12] = 'f';
        wav[13] = 'm';
        wav[14] = 't';
        wav[15] = ' ';
        writeInt(wav, 16, 16); // fmt chunk size
        writeShort(wav, 20, (short) 1); // PCM format
        writeShort(wav, 22, (short) 1); // Mono
        writeInt(wav, 24, sampleRate); // Sample rate
        writeInt(wav, 28, sampleRate * 2); // Byte rate
        writeShort(wav, 32, (short) 2); // Block align
        writeShort(wav, 34, (short) 16); // Bits per sample

        // data chunk
        wav[36] = 'd';
        wav[37] = 'a';
        wav[38] = 't';
        wav[39] = 'a';
        writeInt(wav, 40, numSamples * 2); // Data size

        // PCM data (silence = all zeros, already initialized)

        return wav;
    }

    private void writeInt(byte[] array, int offset, int value) {
        array[offset] = (byte) (value & 0xFF);
        array[offset + 1] = (byte) ((value >> 8) & 0xFF);
        array[offset + 2] = (byte) ((value >> 16) & 0xFF);
        array[offset + 3] = (byte) ((value >> 24) & 0xFF);
    }

    private void writeShort(byte[] array, int offset, short value) {
        array[offset] = (byte) (value & 0xFF);
        array[offset + 1] = (byte) ((value >> 8) & 0xFF);
    }
}
