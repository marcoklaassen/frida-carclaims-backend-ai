package click.klaassen.service;

import com.fasterxml.jackson.annotation.JsonInclude;
import java.util.List;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record AssistantQuestionResponse(
        String question,
        String stepKey,
        String navigateTo,
        List<String> targetFields,
        boolean done,
        boolean recommendPhoto,
        String photoReason,
        String reassignParty,
        boolean driverSameAsHolder,
        boolean otherDriverSameAsHolder) {
}
