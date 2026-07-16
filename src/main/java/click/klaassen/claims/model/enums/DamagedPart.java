package click.klaassen.claims.model.enums;

import com.fasterxml.jackson.annotation.JsonValue;

public enum DamagedPart {
    MOTORHAUBE("Motorhaube"),
    DACH("Dach"),
    KOFFERRAUM_HECKKLAPPE("Kofferraum/Heckklappe"),
    KUEHLERGRILL("Kühlergrill"),
    LINKE_FAHRZEUGSEITE("Linke Fahrzeugseite"),
    RECHTE_FAHRZEUGSEITE("Rechte Fahrzeugseite"),
    VORDERER_STOSSFAENGER("Vorderer Stoßfänger"),
    HINTERER_STOSSFAENGER("Hinterer Stoßfänger"),
    FAHRERTUER_VORNE_LINKS("Fahrertür (vorne links)"),
    BEIFAHRERTUER_VORNE_RECHTS("Beifahrertür (vorne rechts)"),
    HINTERE_LINKE_TUER("Hintere linke Tür"),
    HINTERE_RECHTE_TUER("Hintere rechte Tür"),
    VORDERRAD_LINKS("Vorderrad links"),
    VORDERRAD_RECHTS("Vorderrad rechts"),
    HINTERRAD_LINKS("Hinterrad links"),
    HINTERRAD_RECHTS("Hinterrad rechts"),
    WINDSCHUTZSCHEIBE("Windschutzscheibe"),
    HECKSCHEIBE("Heckscheibe"),
    SEITENSCHEIBE_VORNE_LINKS("Seitenscheibe (vorne links)"),
    SEITENSCHEIBE_VORNE_RECHTS("Seitenscheibe (vorne rechts)"),
    SEITENSCHEIBE_HINTEN_LINKS("Seitenscheibe (hinten links)"),
    SEITENSCHEIBE_HINTEN_RECHTS("Seitenscheibe (hinten rechts)"),
    LINKER_AUSSENSPIEGEL("Linker Außenspiegel"),
    RECHTER_AUSSENSPIEGEL("Rechter Außenspiegel"),
    FRONTSCHEINWERFER_LINKS("Frontscheinwerfer links"),
    FRONTSCHEINWERFER_RECHTS("Frontscheinwerfer rechts"),
    HECKSCHEINWERFER_LINKS("Heckscheinwerfer links"),
    HECKSCHEINWERFER_RECHTS("Heckscheinwerfer rechts"),
    GRIFFSCHALEN_VORNE_LINKS("Griffschalen (vorne links)"),
    GRIFFSCHALEN_VORNE_RECHTS("Griffschalen (vorne rechts)"),
    GRIFFSCHALEN_HINTEN_LINKS("Griffschalen (hinten links)"),
    GRIFFSCHALEN_HINTEN_RECHTS("Griffschalen (hinten rechts)"),
    SCHWELLER_LINKS("Schweller links"),
    SCHWELLER_RECHTS("Schweller rechts"),
    KOTFLUEGEL_LINKS("Kotflügel links"),
    KOTFLUEGEL_RECHTS("Kotflügel rechts");

    private final String value;

    DamagedPart(String value) {
        this.value = value;
    }

    @JsonValue
    public String getValue() {
        return value;
    }
}
