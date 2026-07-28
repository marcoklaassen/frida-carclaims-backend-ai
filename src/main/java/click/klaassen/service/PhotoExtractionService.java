package click.klaassen.service;

import click.klaassen.api.PhotoExtractionResponse;
import click.klaassen.claims.model.Claimsdata;
import click.klaassen.exception.UpstreamAiException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import dev.langchain4j.data.message.ImageContent;
import dev.langchain4j.data.message.SystemMessage;
import dev.langchain4j.data.message.UserMessage;
import dev.langchain4j.model.chat.ChatModel;
import dev.langchain4j.model.chat.request.ChatRequest;
import dev.langchain4j.model.chat.response.ChatResponse;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.Optional;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@ApplicationScoped
public class PhotoExtractionService {

    private static final Logger LOG = Logger.getLogger(PhotoExtractionService.class.getName());
    private static final String PROMPT_RESOURCE = "/frida/photo-extraction-prompt.md";
    static final Pattern GERMAN_PLATE = Pattern.compile(
            "\\b([A-ZÄÖÜ]{1,3})[-\\s]([A-Z]{1,2})\\s?(\\d{1,4})\\b");

    @Inject
    ChatModel chatModel;

    @Inject
    ClaimsFieldExtractor claimsFieldExtractor;

    @Inject
    ClaimsDataMerger claimsDataMerger;

    @Inject
    DemoCustomerDatabase demoCustomerDatabase;

    @Inject
    ObjectMapper objectMapper;

    private volatile String visionPrompt;

    public PhotoExtractionResponse extract(
            byte[] imageBytes, String mimeType, String currentStateJson, String stepKey) {
        String description = describeImage(imageBytes, mimeType);
        String enriched = enrichWithCustomerLookup(description);
        Claimsdata currentState = parseCurrentState(currentStateJson);
        Claimsdata extracted = claimsFieldExtractor.extractFields(enriched, currentStateJson, stepKey);
        Claimsdata merged = claimsDataMerger.merge(currentState, extracted);

        LOG.info("Photo extraction completed: descriptionLength=" + description.length()
                + ", enriched=" + (enriched.length() > description.length()));

        return new PhotoExtractionResponse(description, merged);
    }

    String describeImage(byte[] imageBytes, String mimeType) {
        try {
            String base64 = Base64.getEncoder().encodeToString(imageBytes);
            String mime = mimeType != null ? mimeType : "image/jpeg";

            ImageContent imageContent = ImageContent.from(base64, mime);
            UserMessage userMessage = UserMessage.from(imageContent);

            ChatResponse response = chatModel.chat(ChatRequest.builder()
                    .messages(SystemMessage.from(getVisionPrompt()), userMessage)
                    .build());

            String text = response.aiMessage().text();
            LOG.info("Vision model response length=" + (text != null ? text.length() : 0));
            return text != null ? text : "";
        } catch (Exception e) {
            throw new UpstreamAiException("Photo analysis failed", e);
        }
    }

    String enrichWithCustomerLookup(String description) {
        Matcher matcher = GERMAN_PLATE.matcher(description.toUpperCase());
        StringBuilder enriched = new StringBuilder(description);
        boolean found = false;

        while (matcher.find()) {
            String plate = matcher.group(1) + "-" + matcher.group(2) + " " + matcher.group(3);
            Optional<DemoCustomer> customer = demoCustomerDatabase.findByLicensePlate(plate);
            if (customer.isPresent()) {
                DemoCustomer c = customer.get();
                enriched.append("\n\nKennzeichen ").append(plate).append(" gehört zu: ")
                        .append(c.salutation()).append(" ").append(c.name()).append(" ").append(c.surName())
                        .append(", ").append(c.streetName()).append(" ").append(c.houseNumber())
                        .append(", ").append(c.postalCode()).append(" ").append(c.city())
                        .append(", Tel: ").append(c.telephone())
                        .append(", E-Mail: ").append(c.email())
                        .append(", versichert bei ").append(c.insuranceCompany())
                        .append(" (Versicherungsnummer: ").append(c.insuranceNumber()).append(")")
                        .append(", Fahrzeug: ").append(c.carBrand()).append(" ").append(c.carModel())
                        .append(", Fahrgestellnummer: ").append(c.chassisNumber())
                        .append(", Vollkaskoversicherung: ").append(c.allRiskInsurance() ? "ja" : "nein")
                        .append(".");
                found = true;
                LOG.info("License plate lookup hit: " + plate + " → " + c.surName());
            }
        }
        if (!found) {
            LOG.info("No license plate matched in demo database");
        }
        return enriched.toString();
    }

    private Claimsdata parseCurrentState(String currentStateJson) {
        if (currentStateJson == null || currentStateJson.isBlank()) {
            return null;
        }
        try {
            ObjectMapper lenient = objectMapper.copy()
                    .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
                    .configure(DeserializationFeature.READ_UNKNOWN_ENUM_VALUES_AS_NULL, true);
            return lenient.readValue(currentStateJson, Claimsdata.class);
        } catch (JsonProcessingException e) {
            LOG.warning("Could not parse currentState, ignoring: " + e.getMessage());
            return null;
        }
    }

    private String getVisionPrompt() {
        if (visionPrompt == null) {
            synchronized (this) {
                if (visionPrompt == null) {
                    try (InputStream in = getClass().getResourceAsStream(PROMPT_RESOURCE)) {
                        if (in == null) {
                            throw new IOException("Resource not found: " + PROMPT_RESOURCE);
                        }
                        visionPrompt = new String(in.readAllBytes(), StandardCharsets.UTF_8).trim();
                    } catch (IOException e) {
                        throw new IllegalStateException("Failed to load " + PROMPT_RESOURCE, e);
                    }
                }
            }
        }
        return visionPrompt;
    }
}
