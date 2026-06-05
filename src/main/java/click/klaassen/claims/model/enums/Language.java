package click.klaassen.claims.model.enums;

import com.fasterxml.jackson.annotation.JsonValue;

public enum Language {
    DE("DE"),
    EN("EN"),
    FR("FR"),
    ES("ES"),
    IT("IT"),
    NL("NL"),
    PL("PL");

    private final String value;

    Language(String value) {
        this.value = value;
    }

    @JsonValue
    public String getValue() {
        return value;
    }
}
