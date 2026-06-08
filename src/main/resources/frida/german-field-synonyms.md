# German spoken phrases → FRIDA Claimsdata paths

Map colloquial German from voice transcripts to exact schema field names and enum values.

## Person and roles

| Spoken (German) | Schema path |
|-----------------|-------------|
| Ich heiße … / Mein Name ist … | policyholder.personalInformation.firstName / lastName; if driver: vehicleDriver.personalInformation.* |
| Herr / Frau (Anrede) | *.personalInformation.formOfAddress → `Herr` or `Frau` |
| Versicherungsnehmer, VN, Antragsteller | policyholder.* |
| Fahrer, ich bin gefahren, selbst gefahren | vehicleDriver.* |
| Unfallgegner, andere Partei, Fahrer B | otherVehicleDriver.* / otherPolicyholder.* (only when explicitly mentioned) |
| Zeuge, Zeugin | witness[].personalInformation.* |

## Home vs accident location

| Spoken | Schema path |
|--------|-------------|
| Ich wohne in … / meine Adresse … | policyholder.personalInformation.city, postalCode, streetName, streetNumber |
| Unfallort, Unfall war in …, passiert in … | accidentCity, accidentPostalCode, accidentStreetName, accidentStreetNumber |
| Werkstatt, Garage | vehicleDriver.garageLocation |

## Vehicle and insurance

| Spoken | Schema path |
|--------|-------------|
| Kennzeichen, Nummernschild, amtliches Kennzeichen, Kfz-Kennzeichen | policyholder.vehicleReg |
| Marke, Automarke (BMW, VW, …) | policyholder.vehicleMake |
| Modell, Fahrzeugtyp | policyholder.vehicleType |
| Policennummer, Versicherungsschein, Police, Vertragsnummer | policyholder.policyNumber |
| Versicherer, Versicherungsgesellschaft (HDI, Allianz, …) | policyholder.insuranceCompany |
| Fahrgestellnummer, FIN, VIN | policyholder.vin |
| Kilometerstand, km-Stand | policyholder.currentMileage (integer) |
| Grüne Karte, Grüne-Karte-Nummer | policyholder.greencardNumber |
| Vollkasko, Vollkaskoversichert | policyholder.comprehensiveInsurance |
| Vorsteuerabzug | policyholder.inputTaxDeduction |

## Accident details

| Spoken | Schema path |
|--------|-------------|
| Unfallbeschreibung, was passiert ist, Hergang | accidentDescription |
| Unfalldatum, gestern, am … (date) | accidentDate (YYYY-MM-DD) |
| Unfallzeit, gegen … Uhr, um … | accidentTime (HH:mm:ss) |
| Polizei, Aktenzeichen, Unfallnummer Polizei | accidentPoliceNumber |
| Sachschaden, andere Schäden | hasVehicleDamage, vehicleDamageDescription |
| Verletzte, Verletzung | injuredPerson, injuredPersonNumber |
| Zeugen, es gab Zeugen | witnessExists, witnessCount, witness[] |

## damageCausedBy (exact enum only when clearly stated)

| Spoken variants | Enum value |
|-----------------|------------|
| Auffahren, aufgefahren, Heckschaden durch Auffahren | Auffahren |
| Rangieren, Parken, beim Einparken | Rangieren/Parken |
| Vorfahrt missachtet, Vorfahrt nicht beachtet | Missachtung der Vorfahrt |
| Abbiegen, beim Abbiegen | Abbiegen |
| von der Fahrbahn abgekommen | Abkommen von der Fahrbahn |
| Überholen, Überholvorgang | Überholvorgang |
| Spurwechsel, Spur gewechselt | Spurwechsel |
| Wild, Reh, Wildunfall, Tier | damageCausedBy + typeOfWildlife when applicable |
| Sonstiges, weiß nicht genau | Sonstiges (only if cause explicitly uncertain) |

## driverDamagedpartsGraphic (exact enum labels)

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

Tri-state fields: hasVehicleDamage, injuredPerson, witnessExists, comprehensiveInsurance, inputTaxDeduction, vehicleDrivingAbility, formOfAddress.

## Do not map

- licensePlateNumber, fullName, cityOfAccident, vehicleDriverFirstNames — invented fields; use schema paths above.
- not_specified on damageCausedBy, language, title, driverDamagedpartsGraphic — omit instead.
