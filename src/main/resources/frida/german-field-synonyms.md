# German spoken phrases → FRIDA Claimsdata paths

Map colloquial German from voice transcripts to exact schema field names and enum values.

## Person and roles

| Spoken (German) | Schema field |
|-----------------|-------------|
| Ich heiße … / Mein Name ist … | insuranceHolderSurName / insuranceHolderName; if driver: driverSurName / driverName |
| Herr / Frau (Anrede) | insuranceHolderSalutation, driverSalutation, etc. → `Herr` or `Frau` |
| Versicherungsnehmer, VN, Antragsteller | insuranceHolder* fields |
| Fahrer, ich bin gefahren, selbst gefahren | driver* fields |
| Unfallgegner, andere Partei, Fahrer B | otherDriver* / otherInsuranceHolder* fields (only when explicitly mentioned) |
| Zeuge, Zeugin | witnesses[] fields (salutation, name, surName, etc.) |

## Home vs accident location

| Spoken | Schema field |
|--------|-------------|
| Ich wohne in … / meine Adresse … | insuranceHolderCity, insuranceHolderPostalCode, insuranceHolderStreetName, insuranceHolderHouseNumber |
| Unfallort, Unfall war in …, passiert in … | accidentCity, accidentPostalCode, accidentStreetName, accidentHouseNumber |

## Vehicle and insurance

| Spoken | Schema field |
|--------|-------------|
| Kennzeichen, Nummernschild, amtliches Kennzeichen, Kfz-Kennzeichen | licensePlate |
| Marke, Automarke (BMW, VW, …) | carBrand |
| Modell, Fahrzeugtyp | carModel |
| Policennummer, Versicherungsschein, Police, Vertragsnummer | insuranceNumber |
| Versicherer, Versicherungsgesellschaft (HDI, Allianz, …) | insuranceCompany |
| Fahrgestellnummer, FIN, VIN | chassisNumber |
| Kilometerstand, km-Stand | odometerReading (integer) |
| Grüne Karte, Grüne-Karte-Nummer | greenCardNumber |
| Vollkasko, Vollkaskoversichert | allRiskInsurance |
| Vorsteuerabzug | vatDeduction |

## Accident details

| Spoken | Schema field |
|--------|-------------|
| Unfallbeschreibung, was passiert ist, Hergang | accidentDetails |
| Unfalldatum, gestern, am … (date) | accidentDate (YYYY-MM-DD) |
| Unfallzeit, gegen … Uhr, um … | accidentTime (HH:mm:ss) |
| Polizei, Aktenzeichen, Unfallnummer Polizei | accidentReportNumber |
| Sachschaden, andere Schäden | miscellaneousDamages, miscellaneousDamageDescription |
| Verletzte, Verletzung | hasInjured, injuredCount |
| Zeugen, es gab Zeugen | hasWitnesses, witnessesCount, witnesses[] |

## damageType (exact enum only when clearly stated)

| Spoken variants | Enum value |
|-----------------|------------|
| Auffahren, aufgefahren, Heckschaden durch Auffahren | Auffahren |
| Rangieren, Parken, beim Einparken | Rangieren/Parken |
| Vorfahrt missachtet, Vorfahrt nicht beachtet | Missachtung der Vorfahrt |
| Abbiegen, beim Abbiegen | Abbiegen |
| von der Fahrbahn abgekommen | Abkommen von der Fahrbahn |
| Überholen, Überholvorgang | Überholvorgang |
| Spurwechsel, Spur gewechselt | Spurwechsel |
| Wild, Reh, Wildunfall, Tier | damageType + typeOfWildlife when applicable |
| Sonstiges, weiß nicht genau | Sonstiges (only if cause explicitly uncertain) |

## driverDamagedParts (exact enum labels)

| Spoken | Enum value |
|--------|------------|
| vorne links / rechts | vorne links, vorne rechts |
| Stoßstange vorne / hinten | vorne links or vorne rechts (ask context) or Motorhaube/Kofferraum |
| Fahrertür, Beifahrertür | Fahrertür links, Beifahrertür rechts |
| Motorhaube | Motorhaube |
| Frontscheibe, Windschutzscheibe | Frontscheibe |
| Dach | Dach |
| Heck / hinten | hinten links, hinten rechts, Heckscheibe, Kofferraum |

## Tri-state speech (only on tri-state fields)

| Spoken | Value |
|--------|-------|
| ja, stimmt, korrekt | true |
| nein, nicht, keiner, keine | false |
| weiß nicht, unklar, nicht sicher | not_specified |

Tri-state fields: miscellaneousDamages, hasInjured, hasWitnesses, allRiskInsurance, otherAllRiskInsurance, vatDeduction, otherVatDeduction, vehicleOperational, otherVehicleOperational, insuranceHolderSalutation.

## Do not map

- licensePlateNumber, fullName, cityOfAccident, vehicleDriverFirstNames — invented fields; use schema fields above.
- not_specified on damageType, language, title, driverDamagedParts — omit instead.
