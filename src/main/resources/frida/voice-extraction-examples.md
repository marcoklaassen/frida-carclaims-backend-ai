# Few-shot examples: German transcript → Claimsdata JSON

Return only fields mentioned in the transcript. Use exact enum strings from the schema.

### Example: carclaimsDetails — accident location and time
Transcript: "Der Unfall war gestern gegen halb neun in Köln an der Venloer Straße 12."
JSON:
```json
{"accidentDate":"2026-06-07","accidentTime":"08:30:00","accidentCity":"Köln","accidentStreetName":"Venloer Straße","accidentStreetNumber":"12"}
```

### Example: carclaimsDetails — police reference
Transcript: "Die Polizei hat die Unfallnummer 2024-ABC123 aufgenommen."
JSON:
```json
{"accidentPoliceNumber":"2024-ABC123"}
```

### Example: carclaimsDetails — empty or noise
Transcript: "[Music]"
JSON:
```json
{}
```

### Example: insurance-holder-a — vehicle and policy
Transcript: "Mein Kennzeichen ist BM LD 1234, BMW M5, Versicherung bei HDI, Policennummer V-123-345-678-5."
JSON:
```json
{"policyholder":{"vehicleReg":"BM LD 1234","vehicleMake":"BMW","vehicleType":"M5","insuranceCompany":"HDI","policyNumber":"V-123-345-678-5"}}
```

### Example: insurance-holder-a — name and home address
Transcript: "Ich heiße Max Mustermann und wohne in Bedburg, Germaniastraße 1b, PLZ 50181."
JSON:
```json
{"policyholder":{"personalInformation":{"firstName":"Max","lastName":"Mustermann","city":"Bedburg","streetName":"Germaniastraße","streetNumber":"1b","postalCode":"50181"}}}
```

### Example: driver-a — damage cause
Transcript: "Ich bin aufgefahren, Schaden an der Front."
JSON:
```json
{"vehicleDriver":{"damageCausedBy":"Auffahren","driverVisibleDamage":"Schaden an der Front"}}
```

### Example: driver-a — damaged part
Transcript: "Die Motorhaube ist eingedrückt."
JSON:
```json
{"vehicleDriver":{"driverDamagedpartsGraphic":"Motorhaube","driverVisibleDamage":"Motorhaube eingedrückt"}}
```

### Example: injuredDetails — no injuries
Transcript: "Es gab keine Verletzten."
JSON:
```json
{"injuredPerson":"false"}
```

### Example: injuredDetails — injured count
Transcript: "Eine Person wurde leicht verletzt."
JSON:
```json
{"injuredPerson":"true","injuredPersonNumber":"1"}
```

### Example: miscellaneousDamages — property damage
Transcript: "Es gibt weitere Sachschäden, die Laterne ist kaputt."
JSON:
```json
{"hasVehicleDamage":"true","vehicleDamageDescription":"Laterne beschädigt"}
```

### Example: witness — no witnesses
Transcript: "Es waren keine Zeugen dabei."
JSON:
```json
{"witnessExists":"false"}
```

### Example: witness — one witness
Transcript: "Es gab einen Zeugen, Frau Schmidt aus Düsseldorf."
JSON:
```json
{"witnessExists":"true","witnessCount":"1","witness":[{"personalInformation":{"formOfAddress":"Frau","lastName":"Schmidt","city":"Düsseldorf"}}]}
```

### Example: insurance-holder-b — other party (explicit)
Transcript: "Der Unfallgegner fährt einen Audi A4, Kennzeichen D EF 5678."
JSON:
```json
{"otherPolicyholder":{"vehicleMake":"Audi","vehicleType":"A4","vehicleReg":"D EF 5678"}}
```

### Example: driver-b — only when other driver mentioned
Transcript: "Der andere Fahrer heißt Hans Meyer."
JSON:
```json
{"otherVehicleDriver":{"personalInformation":{"firstName":"Hans","lastName":"Meyer"}}}
```

### Example: negative — do not copy currentState
Transcript: "Unfall in Hamburg."
Current form state has accidentCity Köln — output only Hamburg:
JSON:
```json
{"accidentCity":"Hamburg"}
```
