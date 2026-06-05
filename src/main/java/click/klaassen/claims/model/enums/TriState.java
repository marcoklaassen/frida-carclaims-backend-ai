package click.klaassen.claims.model.enums;

import com.fasterxml.jackson.annotation.JsonValue;

public enum TriState {
    NOT_SPECIFIED("not_specified"),
    FALSE("false"),
    TRUE("true");

    private final String value;

    TriState(String value) {
        this.value = value;
    }

    @JsonValue
    public String getValue() {
        return value;
    }
}
