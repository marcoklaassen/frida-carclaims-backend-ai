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
| Rangieren, Parken, beim Einparken | Rangieren/ Parken |
| Vorfahrt missachtet, Vorfahrt nicht beachtet | Missachtung der Vorfahrt |
| Abbiegen, beim Abbiegen | Abbiegen |
| von der Fahrbahn abgekommen | Abkommen von der Fahrbahn |
| Überholen, Überholvorgang | Überholvorgang |
| Spurwechsel, Spur gewechselt | Spurwechsel |
| Wild, Reh, Wildunfall, Tier | damageType + typeOfWildlife when applicable |
| Sonstiges, weiß nicht genau | Sonstiges (only if cause explicitly uncertain) |

## driverDamagedParts (exact enum labels — use ONLY these values)

| Spoken variants | Enum value |
|-----------------|------------|
| Motorhaube, Haube, Motorabdeckung | Motorhaube |
| Dach, Autodach, Fahrzeugdach | Dach |
| Kofferraum, Heckklappe, Kofferraumdeckel | Kofferraum/Heckklappe |
| Kühlergrill, Grill, Frontgrill | Kühlergrill |
| linke Seite, Fahrzeugseite links, links die ganze Seite | Linke Fahrzeugseite |
| rechte Seite, Fahrzeugseite rechts, rechts die ganze Seite | Rechte Fahrzeugseite |
| Stoßstange vorne, Frontstoßstange, vordere Stoßstange | Vorderer Stoßfänger |
| Stoßstange hinten, Heckstoßstange, hintere Stoßstange | Hinterer Stoßfänger |
| Fahrertür, Tür vorne links | Fahrertür (vorne links) |
| Beifahrertür, Tür vorne rechts | Beifahrertür (vorne rechts) |
| hintere Tür links, Tür hinten links | Hintere linke Tür |
| hintere Tür rechts, Tür hinten rechts | Hintere rechte Tür |
| Vorderrad links, Rad vorne links, Reifen vorne links | Vorderrad links |
| Vorderrad rechts, Rad vorne rechts, Reifen vorne rechts | Vorderrad rechts |
| Hinterrad links, Rad hinten links, Reifen hinten links | Hinterrad links |
| Hinterrad rechts, Rad hinten rechts, Reifen hinten rechts | Hinterrad rechts |
| Windschutzscheibe, Frontscheibe | Windschutzscheibe |
| Heckscheibe, hintere Scheibe, Rückscheibe | Heckscheibe |
| Seitenscheibe vorne links, Fenster vorne links | Seitenscheibe (vorne links) |
| Seitenscheibe vorne rechts, Fenster vorne rechts | Seitenscheibe (vorne rechts) |
| Seitenscheibe hinten links, Fenster hinten links | Seitenscheibe (hinten links) |
| Seitenscheibe hinten rechts, Fenster hinten rechts | Seitenscheibe (hinten rechts) |
| Spiegel links, Außenspiegel links, linker Spiegel | Linker Außenspiegel |
| Spiegel rechts, Außenspiegel rechts, rechter Spiegel | Rechter Außenspiegel |
| Scheinwerfer vorne links, Frontscheinwerfer links | Frontscheinwerfer links |
| Scheinwerfer vorne rechts, Frontscheinwerfer rechts | Frontscheinwerfer rechts |
| Rücklicht links, Heckscheinwerfer links, Rückleuchte links | Heckscheinwerfer links |
| Rücklicht rechts, Heckscheinwerfer rechts, Rückleuchte rechts | Heckscheinwerfer rechts |
| Griffschale vorne links, Türgriff vorne links | Griffschalen (vorne links) |
| Griffschale vorne rechts, Türgriff vorne rechts | Griffschalen (vorne rechts) |
| Griffschale hinten links, Türgriff hinten links | Griffschalen (hinten links) |
| Griffschale hinten rechts, Türgriff hinten rechts | Griffschalen (hinten rechts) |
| Schweller links, Seitenschweller links | Schweller links |
| Schweller rechts, Seitenschweller rechts | Schweller rechts |
| Kotflügel links, linker Kotflügel | Kotflügel links |
| Kotflügel rechts, rechter Kotflügel | Kotflügel rechts |
| hinten rechts (vague) | pick from: Hinterer Stoßfänger, Hintere rechte Tür, Hinterrad rechts, Heckscheinwerfer rechts, Kotflügel rechts — choose the most likely based on context |
| vorne links (vague) | pick from: Vorderer Stoßfänger, Fahrertür (vorne links), Vorderrad links, Frontscheinwerfer links, Kotflügel links — choose the most likely based on context |

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
