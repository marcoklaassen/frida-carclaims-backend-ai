# Voice-to-schema mapping hints

## Person and roles

- Speaker introducing themselves → `insuranceHolderSurName/insuranceHolderName`; if they drove, also `driver*` fields.
- Fahrer A / own driver → `driver*` fields. Fahrer B → `otherDriver*` fields.
- Versicherungsnehmer A → `insuranceHolder*` fields. Other party → `otherInsuranceHolder*` fields.

## Location disambiguation

- Home address ("Ich wohne in Hamburg") → `insuranceHolderCity/insuranceHolderPostalCode/insuranceHolderStreetName/insuranceHolderHouseNumber`.
- Accident location ("Unfall in Köln") → `accidentCity` (and related `accident*` fields), NOT home city.

## Vehicle and insurance

- Make → `carBrand`. Model → `carModel`.
- Kennzeichen → `licensePlate` (never `licensePlateNumber`).
- Policy number → `insuranceNumber`. Insurer → `insuranceCompany`. VIN → `chassisNumber`.

## Accident details

- Description → `accidentDetails`. Date → `accidentDate` (YYYY-MM-DD). Time → `accidentTime` (HH:mm:ss).
- Police ref → `accidentReportNumber`. Cause → `damageType` (exact enum only when stated).
- `damageType` accepts only Schadenursache values (Auffahren, Rangieren/ Parken, …). Omit when not stated — never `not_specified`.

## Tri-state fields only

- ONLY these fields may use `not_specified`, `false`, or `true`: `miscellaneousDamages`, `hasInjured`, `hasWitnesses`, \
`allRiskInsurance`, `vatDeduction`, `vehicleOperational`, and `insuranceHolderSalutation`.
- Do not use `not_specified` for any other field.

## Email addresses

- Voice transcription often renders "@" as "." (e.g. "muster.frau.gmail.com" instead of "muster.frau@gmail.com").
- When an email field value has no "@", fix it by inserting "@" before the mail domain (e.g. gmail.com, web.de, gmx.de, t-online.de, outlook.com, etc.).

## Other party

- Do not populate `otherDriver*` or `otherInsuranceHolder*` fields unless the transcript explicitly mentions the other party or the step hints indicate the current tab is about the other party.

## Witnesses

- Witness details → `witnesses[]` with flat fields per witness (salutation, name, surName, etc.). Count → `witnessesCount`. Exists → `hasWitnesses`.

## Omit

- No binary/image fields unless explicitly described.
- No invented fields like `fullName`, `cityOfAccident`, `licensePlateNumber`.
