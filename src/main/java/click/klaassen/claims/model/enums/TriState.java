package click.klaassen.claims.model.enums;

import com.fasterxml.jackson.annotation.JsonCreator;
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

    @JsonCreator
    public static TriState fromValue(Object value) {
        if (value instanceof Boolean b) {
            return b ? TRUE : FALSE;
        }
        if (value instanceof String s) {
            for (TriState ts : values()) {
                if (ts.value.equalsIgnoreCase(s) || ts.name().equalsIgnoreCase(s)) {
                    return ts;
                }
            }
        }
        return null;
    }
}
