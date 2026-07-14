package click.klaassen.service;

final class ClaimsSchemaPrompt {

    private ClaimsSchemaPrompt() {
    }

    static final String EXTRACTION_RULES = """
            You are an assistant for a German car insurance claim form (FRIDA Car Claims).
            The voice transcript is in German. Map spoken German to exact schema field paths and enum values.

            Output rules:
            - Respond with a single JSON object only. No markdown, no explanation, no thinking.
            - Use ONLY the exact field names from the schema below. Never invent flat fields like fullName, \
            licensePlateNumber, vehicleDriverFirstNames, cityOfAccident, etc.
            - Extract ONLY fields explicitly mentioned or clearly implied in this transcript.
            - Use currentState as context only; do NOT copy unmentioned fields from currentState into the output.
            - If the transcript is empty, unclear, only noise/music (e.g. [Music]), or contains no claim facts, \
            return {}.
            - Map spoken German dates to YYYY-MM-DD and times to HH:mm:ss.
            - Omit image/binary fields unless explicitly described.
            - Use exact flat field names from the catalog. Omit unmentioned fields.
            - Do NOT populate otherDriver*/otherInsuranceHolder* fields unless the transcript explicitly mentions \
            the other party.
            - The value not_specified is ONLY valid for tri-state yes/no fields and salutation fields. Never use it for \
            damageType, language, title, or driverDamagedParts — omit those fields instead.
            - When a step-specific field catalog is provided, extract ONLY fields from that catalog.

            """;
}
