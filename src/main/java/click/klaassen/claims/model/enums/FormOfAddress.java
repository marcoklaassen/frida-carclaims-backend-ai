package click.klaassen.claims.model.enums;

import com.fasterxml.jackson.annotation.JsonValue;

public enum FormOfAddress {
    NOT_SPECIFIED("not_specified"),
    HERR("Herr"),
    FRAU("Frau");

    private final String value;

    FormOfAddress(String value) {
        this.value = value;
    }

    @JsonValue
    public String getValue() {
        return value;
    }
}
