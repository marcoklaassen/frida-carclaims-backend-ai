package click.klaassen.api;

import click.klaassen.claims.model.Claimsdata;
import com.fasterxml.jackson.annotation.JsonInclude;
import io.quarkus.runtime.annotations.RegisterForReflection;

@JsonInclude(JsonInclude.Include.NON_NULL)
@RegisterForReflection
public record AssistantTurnResponse(
        String question,
        String stepKey,
        String navigateTo,
        String audioUrl,
        Claimsdata claimsData,
        String transcript,
        boolean done,
        String photoRecommendation) {
}
