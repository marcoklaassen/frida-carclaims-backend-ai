package click.klaassen.claims.model.enums;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import java.util.Map;

public enum Language {
    DE("Deutsch", "de"),
    EN("English", "en"),
    FR("Français", "fr"),
    ES("Español", "es"),
    IT("Italiano", "it"),
    NL("Nederlands", "nl"),
    PL("Polski", "pl");

    private final String displayName;
    private final String isoCode;

    private static final Map<String, Language> LOOKUP;

    static {
        var map = new java.util.HashMap<String, Language>();
        for (Language lang : values()) {
            map.put(lang.name().toLowerCase(), lang);
            map.put(lang.isoCode.toLowerCase(), lang);
            map.put(lang.displayName.toLowerCase(), lang);
        }
        LOOKUP = Map.copyOf(map);
    }

    Language(String displayName, String isoCode) {
        this.displayName = displayName;
        this.isoCode = isoCode;
    }

    @JsonValue
    public String getDisplayName() {
        return displayName;
    }

    public String getIsoCode() {
        return isoCode;
    }

    @JsonCreator
    public static Language fromValue(String value) {
        if (value == null) return null;
        Language lang = LOOKUP.get(value.toLowerCase());
        if (lang != null) return lang;
        throw new IllegalArgumentException("Unknown language: " + value);
    }
}
