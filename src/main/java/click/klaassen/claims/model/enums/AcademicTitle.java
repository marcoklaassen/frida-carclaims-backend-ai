package click.klaassen.claims.model.enums;

import com.fasterxml.jackson.annotation.JsonValue;

public enum AcademicTitle {
    DR("Dr."),
    DR_DR("Dr. Dr."),
    PROF("Prof.");

    private final String value;

    AcademicTitle(String value) {
        this.value = value;
    }

    @JsonValue
    public String getValue() {
        return value;
    }
}
