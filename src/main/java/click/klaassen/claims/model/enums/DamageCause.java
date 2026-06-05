package click.klaassen.claims.model.enums;

import com.fasterxml.jackson.annotation.JsonValue;

public enum DamageCause {
    AUFFAHREN("Auffahren"),
    RANGIEREN_PARKEN("Rangieren/Parken"),
    MISSACHTUNG_DER_VORFAHRT("Missachtung der Vorfahrt"),
    ABBIEGEN("Abbiegen"),
    ABKOMMEN_VON_DER_FAHBAHN("Abkommen von der Fahrbahn"),
    UEBERHOLVORGANG("Überholvorgang"),
    SPURWECHSEL("Spurwechsel"),
    SONSTIGES("Sonstiges");

    private final String value;

    DamageCause(String value) {
        this.value = value;
    }

    @JsonValue
    public String getValue() {
        return value;
    }
}
