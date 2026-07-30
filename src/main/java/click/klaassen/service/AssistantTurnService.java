package click.klaassen.service;

import click.klaassen.api.AssistantTurnResponse;
import click.klaassen.claims.model.Claimsdata;
import click.klaassen.claims.model.enums.Language;
import click.klaassen.exception.UpstreamAiException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import dev.langchain4j.data.audio.Audio;
import dev.langchain4j.model.audio.AudioTranscriptionModel;
import dev.langchain4j.model.audio.AudioTranscriptionRequest;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import java.util.Base64;
import java.util.logging.Logger;

@ApplicationScoped
public class AssistantTurnService {

    private static final Logger LOG = Logger.getLogger(AssistantTurnService.class.getName());

    private static final java.util.Map<String, String> STEP_KEY_TO_ROUTE = java.util.Map.ofEntries(
        java.util.Map.entry("accident-info", "/accidentinfo"),
        java.util.Map.entry("accident-location", "/accidentlocation"),
        java.util.Map.entry("personal-info-a", "/personalinfo/a"),
        java.util.Map.entry("vehicle-info-a", "/vehicleinfo/a"),
        java.util.Map.entry("driver-info-a", "/driverinfo/a"),
        java.util.Map.entry("damage-location-a", "/damagelocation/a"),
        java.util.Map.entry("damage-description-a", "/damagedescription/a"),
        java.util.Map.entry("personal-info-b", "/personalinfo/b"),
        java.util.Map.entry("vehicle-info-b", "/vehicleinfo/b"),
        java.util.Map.entry("driver-info-b", "/driverinfo/b"),
        java.util.Map.entry("damage-location-b", "/damagelocation/b"),
        java.util.Map.entry("damage-description-b", "/damagedescription/b"),
        java.util.Map.entry("injured-persons", "/injuredpersons"),
        java.util.Map.entry("miscellaneous-damages", "/miscellaneousdamages"),
        java.util.Map.entry("witnesses", "/witnesses")
    );

    @Inject
    AudioTranscriptionModel transcriptionModel;

    @Inject
    ClaimsFieldExtractor claimsFieldExtractor;

    @Inject
    ClaimsDataMerger claimsDataMerger;

    @Inject
    AssistantQuestionService questionService;

    @Inject
    TextToSpeechService ttsService;

    @Inject
    TtsAudioCache ttsAudioCache;

    @Inject
    ObjectMapper objectMapper;

    public AssistantTurnResponse processTurn(
            byte[] audioBytes, String mimeType, String currentStateJson,
            String conversationHistoryJson, String previousStepKey, String previousQuestion) {

        Claimsdata currentState = parseCurrentState(currentStateJson);
        String transcript = null;

        if (audioBytes != null && audioBytes.length > 0) {
            transcript = transcribe(audioBytes, mimeType);
            String enriched = enrichTranscriptWithContext(transcript, previousQuestion);
            Claimsdata extracted = claimsFieldExtractor.extractFields(
                    enriched, currentStateJson, previousStepKey);
            currentState = claimsDataMerger.merge(currentState, extracted);
        }

        String updatedStateJson = serializeState(currentState);
        AssistantQuestionResponse questionResponse =
                questionService.generateNextQuestion(updatedStateJson, conversationHistoryJson);

        if ("b".equals(questionResponse.reassignParty())) {
            currentState = swapPartyFields(currentState);
        }

        if (questionResponse.driverSameAsHolder()) {
            copyHolderToDriver(currentState);
        }
        if (questionResponse.otherDriverSameAsHolder()) {
            copyOtherHolderToOtherDriver(currentState);
        }

        String audioUrl = null;
        if (questionResponse.question() != null && !questionResponse.question().isBlank()) {
            byte[] ttsAudio = ttsService.generateSpeech(questionResponse.question());
            String audioId = ttsAudioCache.store(ttsAudio);
            audioUrl = "/api/assistant/audio/" + audioId;
        }

        LOG.info("Assistant turn completed: transcript="
                + (transcript != null ? transcript.length() : 0)
                + " chars, nextStep=" + questionResponse.stepKey()
                + ", done=" + questionResponse.done());

        String navigateTo = STEP_KEY_TO_ROUTE.getOrDefault(
                questionResponse.stepKey(), questionResponse.navigateTo());

        return new AssistantTurnResponse(
                questionResponse.question(),
                questionResponse.stepKey(),
                navigateTo,
                audioUrl,
                currentState,
                transcript,
                questionResponse.done(),
                questionResponse.recommendPhoto()
                        ? questionResponse.photoReason() : null);
    }

    static String enrichTranscriptWithContext(String transcript, String previousQuestion) {
        if (previousQuestion == null || previousQuestion.isBlank()) {
            return transcript;
        }
        return "Der Assistent hat gefragt: \"" + previousQuestion
                + "\"\nDie Antwort war: \"" + transcript + "\"";
    }

    private String transcribe(byte[] audioBytes, String mimeType) {
        try {
            String base64 = Base64.getEncoder().encodeToString(audioBytes);
            String mime = mimeType != null ? mimeType : "audio/webm";
            Audio audio = Audio.builder()
                    .base64Data(base64)
                    .mimeType(mime)
                    .build();
            AudioTranscriptionRequest request = AudioTranscriptionRequest.builder(audio)
                    .language(Language.DE.getIsoCode())
                    .build();
            return transcriptionModel.transcribe(request).text();
        } catch (Exception e) {
            throw new UpstreamAiException("Audio transcription failed", e);
        }
    }

    private Claimsdata parseCurrentState(String json) {
        if (json == null || json.isBlank()) {
            return null;
        }
        try {
            ObjectMapper lenient = objectMapper.copy()
                    .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
                    .configure(DeserializationFeature.READ_UNKNOWN_ENUM_VALUES_AS_NULL, true);
            return lenient.readValue(json, Claimsdata.class);
        } catch (JsonProcessingException e) {
            LOG.warning("Could not parse currentState: " + e.getMessage());
            return null;
        }
    }

    private String serializeState(Claimsdata state) {
        if (state == null) {
            return "{}";
        }
        try {
            return objectMapper.writeValueAsString(state);
        } catch (JsonProcessingException e) {
            LOG.warning("Could not serialize state: " + e.getMessage());
            return "{}";
        }
    }

    private void copyHolderToDriver(Claimsdata data) {
        if (data == null) return;
        data.setDriverSalutation(data.getInsuranceHolderSalutation());
        data.setDriverName(data.getInsuranceHolderName());
        data.setDriverSurName(data.getInsuranceHolderSurName());
    }

    private void copyOtherHolderToOtherDriver(Claimsdata data) {
        if (data == null) return;
        data.setOtherDriverSalutation(data.getOtherInsuranceHolderSalutation());
        data.setOtherDriverName(data.getOtherInsuranceHolderName());
        data.setOtherDriverSurName(data.getOtherInsuranceHolderSurName());
    }

    Claimsdata swapPartyFields(Claimsdata data) {
        if (data == null) {
            return null;
        }

        // Insurance holder fields (String)
        String tempSalutation = data.getInsuranceHolderSalutation();
        data.setInsuranceHolderSalutation(data.getOtherInsuranceHolderSalutation());
        data.setOtherInsuranceHolderSalutation(tempSalutation);

        String tempTitle = data.getInsuranceHolderTitle();
        data.setInsuranceHolderTitle(data.getOtherInsuranceHolderTitle());
        data.setOtherInsuranceHolderTitle(tempTitle);

        String tempName = data.getInsuranceHolderName();
        data.setInsuranceHolderName(data.getOtherInsuranceHolderName());
        data.setOtherInsuranceHolderName(tempName);

        String tempSurName = data.getInsuranceHolderSurName();
        data.setInsuranceHolderSurName(data.getOtherInsuranceHolderSurName());
        data.setOtherInsuranceHolderSurName(tempSurName);

        String tempStreetName = data.getInsuranceHolderStreetName();
        data.setInsuranceHolderStreetName(data.getOtherInsuranceHolderStreetName());
        data.setOtherInsuranceHolderStreetName(tempStreetName);

        String tempHouseNumber = data.getInsuranceHolderHouseNumber();
        data.setInsuranceHolderHouseNumber(data.getOtherInsuranceHolderHouseNumber());
        data.setOtherInsuranceHolderHouseNumber(tempHouseNumber);

        String tempPostalCode = data.getInsuranceHolderPostalCode();
        data.setInsuranceHolderPostalCode(data.getOtherInsuranceHolderPostalCode());
        data.setOtherInsuranceHolderPostalCode(tempPostalCode);

        String tempCity = data.getInsuranceHolderCity();
        data.setInsuranceHolderCity(data.getOtherInsuranceHolderCity());
        data.setOtherInsuranceHolderCity(tempCity);

        String tempTelephone = data.getInsuranceHolderTelephone();
        data.setInsuranceHolderTelephone(data.getOtherInsuranceHolderTelephone());
        data.setOtherInsuranceHolderTelephone(tempTelephone);

        String tempEmail = data.getInsuranceHolderEmail();
        data.setInsuranceHolderEmail(data.getOtherInsuranceHolderEmail());
        data.setOtherInsuranceHolderEmail(tempEmail);

        // Vehicle/insurance fields (String)
        String tempCarBrand = data.getCarBrand();
        data.setCarBrand(data.getOtherCarBrand());
        data.setOtherCarBrand(tempCarBrand);

        String tempCarModel = data.getCarModel();
        data.setCarModel(data.getOtherCarModel());
        data.setOtherCarModel(tempCarModel);

        String tempLicensePlate = data.getLicensePlate();
        data.setLicensePlate(data.getOtherLicensePlate());
        data.setOtherLicensePlate(tempLicensePlate);

        String tempInsuranceCompany = data.getInsuranceCompany();
        data.setInsuranceCompany(data.getOtherInsuranceCompany());
        data.setOtherInsuranceCompany(tempInsuranceCompany);

        String tempInsuranceNumber = data.getInsuranceNumber();
        data.setInsuranceNumber(data.getOtherInsuranceNumber());
        data.setOtherInsuranceNumber(tempInsuranceNumber);

        String tempChassisNumber = data.getChassisNumber();
        data.setChassisNumber(data.getOtherChassisNumber());
        data.setOtherChassisNumber(tempChassisNumber);

        String tempOdometerReading = data.getOdometerReading();
        data.setOdometerReading(data.getOtherOdometerReading());
        data.setOtherOdometerReading(tempOdometerReading);

        String tempGreenCardNumber = data.getGreenCardNumber();
        data.setGreenCardNumber(data.getOtherGreenCardNumber());
        data.setOtherGreenCardNumber(tempGreenCardNumber);

        String tempValidDateGreenCard = data.getValidDateGreenCard();
        data.setValidDateGreenCard(data.getOtherValidDateGreenCard());
        data.setOtherValidDateGreenCard(tempValidDateGreenCard);

        // Vehicle/insurance fields (TriState)
        click.klaassen.claims.model.enums.TriState tempVatDeduction = data.getVatDeduction();
        data.setVatDeduction(data.getOtherVatDeduction());
        data.setOtherVatDeduction(tempVatDeduction);

        click.klaassen.claims.model.enums.TriState tempAllRiskInsurance = data.getAllRiskInsurance();
        data.setAllRiskInsurance(data.getOtherAllRiskInsurance());
        data.setOtherAllRiskInsurance(tempAllRiskInsurance);

        // Driver fields (String)
        String tempDriverSalutation = data.getDriverSalutation();
        data.setDriverSalutation(data.getOtherDriverSalutation());
        data.setOtherDriverSalutation(tempDriverSalutation);

        String tempDriverName = data.getDriverName();
        data.setDriverName(data.getOtherDriverName());
        data.setOtherDriverName(tempDriverName);

        String tempDriverSurName = data.getDriverSurName();
        data.setDriverSurName(data.getOtherDriverSurName());
        data.setOtherDriverSurName(tempDriverSurName);

        String tempDriverStreetName = data.getDriverStreetName();
        data.setDriverStreetName(data.getOtherDriverStreetName());
        data.setOtherDriverStreetName(tempDriverStreetName);

        String tempDriverHouseNumber = data.getDriverHouseNumber();
        data.setDriverHouseNumber(data.getOtherDriverHouseNumber());
        data.setOtherDriverHouseNumber(tempDriverHouseNumber);

        String tempDriverPostalCode = data.getDriverPostalCode();
        data.setDriverPostalCode(data.getOtherDriverPostalCode());
        data.setOtherDriverPostalCode(tempDriverPostalCode);

        String tempDriverCity = data.getDriverCity();
        data.setDriverCity(data.getOtherDriverCity());
        data.setOtherDriverCity(tempDriverCity);

        String tempDriverTelephone = data.getDriverTelephone();
        data.setDriverTelephone(data.getOtherDriverTelephone());
        data.setOtherDriverTelephone(tempDriverTelephone);

        String tempDriverEmail = data.getDriverEmail();
        data.setDriverEmail(data.getOtherDriverEmail());
        data.setOtherDriverEmail(tempDriverEmail);

        String tempDriverLicense = data.getDriverDriverLicense();
        data.setDriverDriverLicense(data.getOtherDriverDriverLicense());
        data.setOtherDriverDriverLicense(tempDriverLicense);

        String tempLicenseIssuingAuthority = data.getDriverLicenseIssuingAuthority();
        data.setDriverLicenseIssuingAuthority(data.getOtherDriverLicenseIssuingAuthority());
        data.setOtherDriverLicenseIssuingAuthority(tempLicenseIssuingAuthority);

        // Driver fields (List<String>)
        java.util.List<String> tempDamagedParts = data.getDriverDamagedParts();
        data.setDriverDamagedParts(data.getOtherDriverDamagedParts());
        data.setOtherDriverDamagedParts(tempDamagedParts);

        // Damage fields (String)
        String tempDamageDescription = data.getDamageDescription();
        data.setDamageDescription(data.getOtherDamageDescription());
        data.setOtherDamageDescription(tempDamageDescription);

        String tempAdditionalComments = data.getAdditionalComments();
        data.setAdditionalComments(data.getOtherAdditionalComments());
        data.setOtherAdditionalComments(tempAdditionalComments);

        String tempDamageType = data.getDamageType();
        data.setDamageType(data.getOtherDamageType());
        data.setOtherDamageType(tempDamageType);

        // Damage fields (TriState)
        click.klaassen.claims.model.enums.TriState tempVehicleOperational = data.getVehicleOperational();
        data.setVehicleOperational(data.getOtherVehicleOperational());
        data.setOtherVehicleOperational(tempVehicleOperational);

        return data;
    }
}
