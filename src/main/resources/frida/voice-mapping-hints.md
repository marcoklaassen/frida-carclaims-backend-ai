# Voice-to-schema mapping hints

## Person and roles

- Speaker introducing themselves → `policyholder.personalInformation.firstName/lastName`; if they drove, also `vehicleDriver.personalInformation.*`.
- Fahrer A / own driver → `vehicleDriver`. Fahrer B → `otherVehicleDriver`.
- Versicherungsnehmer A → `policyholder`. Other party → `otherPolicyholder`.

## Location disambiguation

- Home address ("Ich wohne in Hamburg") → `policyholder.personalInformation.city/postalCode/streetName/streetNumber`.
- Accident location ("Unfall in Köln") → `accidentCity` (and related `accident*` fields), NOT home city.
- Workshop → `vehicleDriver.garageLocation` or `otherVehicleDriver.garageLocation`.

## Vehicle and insurance

- Make → `policyholder.vehicleMake`. Model → `policyholder.vehicleType`.
- Kennzeichen → `policyholder.vehicleReg` (never `licensePlateNumber`).
- Policy number → `policyholder.policyNumber`. Insurer → `policyholder.insuranceCompany`. VIN → `policyholder.vin`.

## Accident details

- Description → `accidentDescription`. Date → `accidentDate` (YYYY-MM-DD). Time → `accidentTime` (HH:mm:ss).
- Police ref → `accidentPoliceNumber`. Cause → `vehicleDriver.damageCausedBy` (exact enum only when stated). Wildlife → `vehicleDriver.typeOfWildlife`.
- `damageCausedBy` accepts only Schadenursache values (Auffahren, Rangieren/Parken, …). Omit when not stated — never `not_specified`.

## Tri-state fields only

- ONLY these fields may use `not_specified`, `false`, or `true`: `hasVehicleDamage`, `injuredPerson`, `witnessExists`, \
`comprehensiveInsurance`, `inputTaxDeduction`, `vehicleDrivingAbility`, and `formOfAddress`.
- Do not use `not_specified` for any other field.

## Other party

- Do not populate `otherVehicleDriver` or `otherPolicyholder` unless the transcript explicitly mentions the other party.

## Witnesses

- Witness details → `witness[]` with `personalInformation`. Count → `witnessCount`. Exists → `witnessExists`.

## Omit

- No binary/image fields unless explicitly described.
- No invented fields like `fullName`, `cityOfAccident`, `licensePlateNumber`.
