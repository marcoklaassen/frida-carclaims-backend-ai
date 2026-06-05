package click.klaassen.claims.model.enums;

import com.fasterxml.jackson.annotation.JsonValue;

public enum DamagedPart {
    VORNE_LINKS("vorne links"),
    VORNE_RECHTS("vorne rechts"),
    SEITE_VORNE_LINKS("Seite vorne links"),
    SEITE_VORNE_RECHTS("Seite vorne rechts"),
    FAHRERTUER_LINKS("Fahrertür links"),
    BEIFAHRERTUER_RECHTS("Beifahrertür rechts"),
    HINTERE_TUER_LINKS("hintere Tür links"),
    HINTERE_TUER_RECHTS("hintere Tür rechts"),
    SEITE_HINTEN_LINKS("Seite hinten links"),
    SEITE_HINTEN_RECHTS("Seite hinten rechts"),
    HINTEN_LINKS("hinten links"),
    HINTEN_RECHTS("hinten rechts"),
    MOTORHAUBE("Motorhaube"),
    FRONTSCHEIBE("Frontscheibe"),
    DACH("Dach"),
    HECKSCHEIBE("Heckscheibe"),
    KOFFERRAUM("Kofferraum");

    private final String value;

    DamagedPart(String value) {
        this.value = value;
    }

    @JsonValue
    public String getValue() {
        return value;
    }
}
