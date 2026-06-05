package click.klaassen.api;

import click.klaassen.claims.model.Claimsdata;
import com.fasterxml.jackson.annotation.JsonInclude;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record VoiceExtractionResponse(String transcript, Claimsdata claimsData) {
}
