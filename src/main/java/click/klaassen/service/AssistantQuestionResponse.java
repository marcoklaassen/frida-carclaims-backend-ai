package click.klaassen.service;

import com.fasterxml.jackson.annotation.JsonInclude;
import io.quarkus.runtime.annotations.RegisterForReflection;
import java.util.List;

@JsonInclude(JsonInclude.Include.NON_NULL)
@RegisterForReflection
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
