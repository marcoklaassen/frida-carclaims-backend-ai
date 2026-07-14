package click.klaassen.claims.model;

import click.klaassen.claims.model.enums.Language;
import click.klaassen.claims.model.enums.TriState;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import java.util.List;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public class Claimsdata {

    // --- General ---
    private Language language;

    // --- Accident ---
    private String accidentDate;
    private String accidentTime;
    private String accidentPostalCode;
    private String accidentCity;
    private String accidentStreetName;
    private String accidentHouseNumber;
    private String accidentDetails;
    private String accidentReportNumber;

    // --- Miscellaneous damages ---
    private TriState miscellaneousDamages;
    private String miscellaneousDamageDescription;

    // --- Injured ---
    private TriState hasInjured;
    private String injuredCount;

    // --- Witnesses ---
    private TriState hasWitnesses;
    private String witnessesCount;
    private List<WitnessDetails> witnesses;

    // --- Insurance holder A ---
    private String insuranceHolderSalutation;
    private String insuranceHolderTitle;
    private String insuranceHolderName;
    private String insuranceHolderSurName;
    private String insuranceHolderStreetName;
    private String insuranceHolderHouseNumber;
    private String insuranceHolderPostalCode;
    private String insuranceHolderCity;
    private String insuranceHolderTelephone;
    private String insuranceHolderEmail;
    private TriState vatDeduction;
    private String carBrand;
    private String carModel;
    private String licensePlate;
    private String insuranceCompany;
    private String insuranceNumber;
    private String chassisNumber;
    private String odometerReading;
    private String greenCardNumber;
    private String validDateGreenCard;
    private TriState allRiskInsurance;

    // --- Insurance holder B ---
    private String otherInsuranceHolderSalutation;
    private String otherInsuranceHolderTitle;
    private String otherInsuranceHolderName;
    private String otherInsuranceHolderSurName;
    private String otherInsuranceHolderStreetName;
    private String otherInsuranceHolderHouseNumber;
    private String otherInsuranceHolderPostalCode;
    private String otherInsuranceHolderCity;
    private String otherInsuranceHolderTelephone;
    private String otherInsuranceHolderEmail;
    private TriState otherVatDeduction;
    private String otherCarBrand;
    private String otherCarModel;
    private String otherLicensePlate;
    private String otherInsuranceCompany;
    private String otherInsuranceNumber;
    private String otherChassisNumber;
    private String otherOdometerReading;
    private String otherGreenCardNumber;
    private String otherValidDateGreenCard;
    private TriState otherAllRiskInsurance;

    // --- Driver A ---
    private String driverSalutation;
    private String driverName;
    private String driverSurName;
    private String driverStreetName;
    private String driverHouseNumber;
    private String driverPostalCode;
    private String driverCity;
    private String driverTelephone;
    private String driverEmail;
    private String driverDriverLicense;
    private String driverLicenseIssuingAuthority;
    private List<String> driverDamagedParts;
    private String damageDescription;
    private String additionalComments;
    private TriState vehicleOperational;
    private String damageType;

    // --- Driver B ---
    private String otherDriverSalutation;
    private String otherDriverName;
    private String otherDriverSurName;
    private String otherDriverStreetName;
    private String otherDriverHouseNumber;
    private String otherDriverPostalCode;
    private String otherDriverCity;
    private String otherDriverTelephone;
    private String otherDriverEmail;
    private String otherDriverDriverLicense;
    private String otherDriverLicenseIssuingAuthority;
    private List<String> otherDriverDamagedParts;
    private String otherDamageDescription;
    private String otherAdditionalComments;
    private TriState otherVehicleOperational;
    private String otherDamageType;

    public Language getLanguage() { return language; }
    public void setLanguage(Language language) { this.language = language; }

    public String getAccidentDate() { return accidentDate; }
    public void setAccidentDate(String accidentDate) { this.accidentDate = accidentDate; }

    public String getAccidentTime() { return accidentTime; }
    public void setAccidentTime(String accidentTime) { this.accidentTime = accidentTime; }

    public String getAccidentPostalCode() { return accidentPostalCode; }
    public void setAccidentPostalCode(String accidentPostalCode) { this.accidentPostalCode = accidentPostalCode; }

    public String getAccidentCity() { return accidentCity; }
    public void setAccidentCity(String accidentCity) { this.accidentCity = accidentCity; }

    public String getAccidentStreetName() { return accidentStreetName; }
    public void setAccidentStreetName(String accidentStreetName) { this.accidentStreetName = accidentStreetName; }

    public String getAccidentHouseNumber() { return accidentHouseNumber; }
    public void setAccidentHouseNumber(String accidentHouseNumber) { this.accidentHouseNumber = accidentHouseNumber; }

    public String getAccidentDetails() { return accidentDetails; }
    public void setAccidentDetails(String accidentDetails) { this.accidentDetails = accidentDetails; }

    public String getAccidentReportNumber() { return accidentReportNumber; }
    public void setAccidentReportNumber(String accidentReportNumber) { this.accidentReportNumber = accidentReportNumber; }

    public TriState getMiscellaneousDamages() { return miscellaneousDamages; }
    public void setMiscellaneousDamages(TriState miscellaneousDamages) { this.miscellaneousDamages = miscellaneousDamages; }

    public String getMiscellaneousDamageDescription() { return miscellaneousDamageDescription; }
    public void setMiscellaneousDamageDescription(String miscellaneousDamageDescription) { this.miscellaneousDamageDescription = miscellaneousDamageDescription; }

    public TriState getHasInjured() { return hasInjured; }
    public void setHasInjured(TriState hasInjured) { this.hasInjured = hasInjured; }

    public String getInjuredCount() { return injuredCount; }
    public void setInjuredCount(String injuredCount) { this.injuredCount = injuredCount; }

    public TriState getHasWitnesses() { return hasWitnesses; }
    public void setHasWitnesses(TriState hasWitnesses) { this.hasWitnesses = hasWitnesses; }

    public String getWitnessesCount() { return witnessesCount; }
    public void setWitnessesCount(String witnessesCount) { this.witnessesCount = witnessesCount; }

    public List<WitnessDetails> getWitnesses() { return witnesses; }
    public void setWitnesses(List<WitnessDetails> witnesses) { this.witnesses = witnesses; }

    public String getInsuranceHolderSalutation() { return insuranceHolderSalutation; }
    public void setInsuranceHolderSalutation(String insuranceHolderSalutation) { this.insuranceHolderSalutation = insuranceHolderSalutation; }

    public String getInsuranceHolderTitle() { return insuranceHolderTitle; }
    public void setInsuranceHolderTitle(String insuranceHolderTitle) { this.insuranceHolderTitle = insuranceHolderTitle; }

    public String getInsuranceHolderName() { return insuranceHolderName; }
    public void setInsuranceHolderName(String insuranceHolderName) { this.insuranceHolderName = insuranceHolderName; }

    public String getInsuranceHolderSurName() { return insuranceHolderSurName; }
    public void setInsuranceHolderSurName(String insuranceHolderSurName) { this.insuranceHolderSurName = insuranceHolderSurName; }

    public String getInsuranceHolderStreetName() { return insuranceHolderStreetName; }
    public void setInsuranceHolderStreetName(String insuranceHolderStreetName) { this.insuranceHolderStreetName = insuranceHolderStreetName; }

    public String getInsuranceHolderHouseNumber() { return insuranceHolderHouseNumber; }
    public void setInsuranceHolderHouseNumber(String insuranceHolderHouseNumber) { this.insuranceHolderHouseNumber = insuranceHolderHouseNumber; }

    public String getInsuranceHolderPostalCode() { return insuranceHolderPostalCode; }
    public void setInsuranceHolderPostalCode(String insuranceHolderPostalCode) { this.insuranceHolderPostalCode = insuranceHolderPostalCode; }

    public String getInsuranceHolderCity() { return insuranceHolderCity; }
    public void setInsuranceHolderCity(String insuranceHolderCity) { this.insuranceHolderCity = insuranceHolderCity; }

    public String getInsuranceHolderTelephone() { return insuranceHolderTelephone; }
    public void setInsuranceHolderTelephone(String insuranceHolderTelephone) { this.insuranceHolderTelephone = insuranceHolderTelephone; }

    public String getInsuranceHolderEmail() { return insuranceHolderEmail; }
    public void setInsuranceHolderEmail(String insuranceHolderEmail) { this.insuranceHolderEmail = insuranceHolderEmail; }

    public TriState getVatDeduction() { return vatDeduction; }
    public void setVatDeduction(TriState vatDeduction) { this.vatDeduction = vatDeduction; }

    public String getCarBrand() { return carBrand; }
    public void setCarBrand(String carBrand) { this.carBrand = carBrand; }

    public String getCarModel() { return carModel; }
    public void setCarModel(String carModel) { this.carModel = carModel; }

    public String getLicensePlate() { return licensePlate; }
    public void setLicensePlate(String licensePlate) { this.licensePlate = licensePlate; }

    public String getInsuranceCompany() { return insuranceCompany; }
    public void setInsuranceCompany(String insuranceCompany) { this.insuranceCompany = insuranceCompany; }

    public String getInsuranceNumber() { return insuranceNumber; }
    public void setInsuranceNumber(String insuranceNumber) { this.insuranceNumber = insuranceNumber; }

    public String getChassisNumber() { return chassisNumber; }
    public void setChassisNumber(String chassisNumber) { this.chassisNumber = chassisNumber; }

    public String getOdometerReading() { return odometerReading; }
    public void setOdometerReading(String odometerReading) { this.odometerReading = odometerReading; }

    public String getGreenCardNumber() { return greenCardNumber; }
    public void setGreenCardNumber(String greenCardNumber) { this.greenCardNumber = greenCardNumber; }

    public String getValidDateGreenCard() { return validDateGreenCard; }
    public void setValidDateGreenCard(String validDateGreenCard) { this.validDateGreenCard = validDateGreenCard; }

    public TriState getAllRiskInsurance() { return allRiskInsurance; }
    public void setAllRiskInsurance(TriState allRiskInsurance) { this.allRiskInsurance = allRiskInsurance; }

    public String getOtherInsuranceHolderSalutation() { return otherInsuranceHolderSalutation; }
    public void setOtherInsuranceHolderSalutation(String otherInsuranceHolderSalutation) { this.otherInsuranceHolderSalutation = otherInsuranceHolderSalutation; }

    public String getOtherInsuranceHolderTitle() { return otherInsuranceHolderTitle; }
    public void setOtherInsuranceHolderTitle(String otherInsuranceHolderTitle) { this.otherInsuranceHolderTitle = otherInsuranceHolderTitle; }

    public String getOtherInsuranceHolderName() { return otherInsuranceHolderName; }
    public void setOtherInsuranceHolderName(String otherInsuranceHolderName) { this.otherInsuranceHolderName = otherInsuranceHolderName; }

    public String getOtherInsuranceHolderSurName() { return otherInsuranceHolderSurName; }
    public void setOtherInsuranceHolderSurName(String otherInsuranceHolderSurName) { this.otherInsuranceHolderSurName = otherInsuranceHolderSurName; }

    public String getOtherInsuranceHolderStreetName() { return otherInsuranceHolderStreetName; }
    public void setOtherInsuranceHolderStreetName(String otherInsuranceHolderStreetName) { this.otherInsuranceHolderStreetName = otherInsuranceHolderStreetName; }

    public String getOtherInsuranceHolderHouseNumber() { return otherInsuranceHolderHouseNumber; }
    public void setOtherInsuranceHolderHouseNumber(String otherInsuranceHolderHouseNumber) { this.otherInsuranceHolderHouseNumber = otherInsuranceHolderHouseNumber; }

    public String getOtherInsuranceHolderPostalCode() { return otherInsuranceHolderPostalCode; }
    public void setOtherInsuranceHolderPostalCode(String otherInsuranceHolderPostalCode) { this.otherInsuranceHolderPostalCode = otherInsuranceHolderPostalCode; }

    public String getOtherInsuranceHolderCity() { return otherInsuranceHolderCity; }
    public void setOtherInsuranceHolderCity(String otherInsuranceHolderCity) { this.otherInsuranceHolderCity = otherInsuranceHolderCity; }

    public String getOtherInsuranceHolderTelephone() { return otherInsuranceHolderTelephone; }
    public void setOtherInsuranceHolderTelephone(String otherInsuranceHolderTelephone) { this.otherInsuranceHolderTelephone = otherInsuranceHolderTelephone; }

    public String getOtherInsuranceHolderEmail() { return otherInsuranceHolderEmail; }
    public void setOtherInsuranceHolderEmail(String otherInsuranceHolderEmail) { this.otherInsuranceHolderEmail = otherInsuranceHolderEmail; }

    public TriState getOtherVatDeduction() { return otherVatDeduction; }
    public void setOtherVatDeduction(TriState otherVatDeduction) { this.otherVatDeduction = otherVatDeduction; }

    public String getOtherCarBrand() { return otherCarBrand; }
    public void setOtherCarBrand(String otherCarBrand) { this.otherCarBrand = otherCarBrand; }

    public String getOtherCarModel() { return otherCarModel; }
    public void setOtherCarModel(String otherCarModel) { this.otherCarModel = otherCarModel; }

    public String getOtherLicensePlate() { return otherLicensePlate; }
    public void setOtherLicensePlate(String otherLicensePlate) { this.otherLicensePlate = otherLicensePlate; }

    public String getOtherInsuranceCompany() { return otherInsuranceCompany; }
    public void setOtherInsuranceCompany(String otherInsuranceCompany) { this.otherInsuranceCompany = otherInsuranceCompany; }

    public String getOtherInsuranceNumber() { return otherInsuranceNumber; }
    public void setOtherInsuranceNumber(String otherInsuranceNumber) { this.otherInsuranceNumber = otherInsuranceNumber; }

    public String getOtherChassisNumber() { return otherChassisNumber; }
    public void setOtherChassisNumber(String otherChassisNumber) { this.otherChassisNumber = otherChassisNumber; }

    public String getOtherOdometerReading() { return otherOdometerReading; }
    public void setOtherOdometerReading(String otherOdometerReading) { this.otherOdometerReading = otherOdometerReading; }

    public String getOtherGreenCardNumber() { return otherGreenCardNumber; }
    public void setOtherGreenCardNumber(String otherGreenCardNumber) { this.otherGreenCardNumber = otherGreenCardNumber; }

    public String getOtherValidDateGreenCard() { return otherValidDateGreenCard; }
    public void setOtherValidDateGreenCard(String otherValidDateGreenCard) { this.otherValidDateGreenCard = otherValidDateGreenCard; }

    public TriState getOtherAllRiskInsurance() { return otherAllRiskInsurance; }
    public void setOtherAllRiskInsurance(TriState otherAllRiskInsurance) { this.otherAllRiskInsurance = otherAllRiskInsurance; }

    public String getDriverSalutation() { return driverSalutation; }
    public void setDriverSalutation(String driverSalutation) { this.driverSalutation = driverSalutation; }

    public String getDriverName() { return driverName; }
    public void setDriverName(String driverName) { this.driverName = driverName; }

    public String getDriverSurName() { return driverSurName; }
    public void setDriverSurName(String driverSurName) { this.driverSurName = driverSurName; }

    public String getDriverStreetName() { return driverStreetName; }
    public void setDriverStreetName(String driverStreetName) { this.driverStreetName = driverStreetName; }

    public String getDriverHouseNumber() { return driverHouseNumber; }
    public void setDriverHouseNumber(String driverHouseNumber) { this.driverHouseNumber = driverHouseNumber; }

    public String getDriverPostalCode() { return driverPostalCode; }
    public void setDriverPostalCode(String driverPostalCode) { this.driverPostalCode = driverPostalCode; }

    public String getDriverCity() { return driverCity; }
    public void setDriverCity(String driverCity) { this.driverCity = driverCity; }

    public String getDriverTelephone() { return driverTelephone; }
    public void setDriverTelephone(String driverTelephone) { this.driverTelephone = driverTelephone; }

    public String getDriverEmail() { return driverEmail; }
    public void setDriverEmail(String driverEmail) { this.driverEmail = driverEmail; }

    public String getDriverDriverLicense() { return driverDriverLicense; }
    public void setDriverDriverLicense(String driverDriverLicense) { this.driverDriverLicense = driverDriverLicense; }

    public String getDriverLicenseIssuingAuthority() { return driverLicenseIssuingAuthority; }
    public void setDriverLicenseIssuingAuthority(String driverLicenseIssuingAuthority) { this.driverLicenseIssuingAuthority = driverLicenseIssuingAuthority; }

    public List<String> getDriverDamagedParts() { return driverDamagedParts; }
    public void setDriverDamagedParts(List<String> driverDamagedParts) { this.driverDamagedParts = driverDamagedParts; }

    public String getDamageDescription() { return damageDescription; }
    public void setDamageDescription(String damageDescription) { this.damageDescription = damageDescription; }

    public String getAdditionalComments() { return additionalComments; }
    public void setAdditionalComments(String additionalComments) { this.additionalComments = additionalComments; }

    public TriState getVehicleOperational() { return vehicleOperational; }
    public void setVehicleOperational(TriState vehicleOperational) { this.vehicleOperational = vehicleOperational; }

    public String getDamageType() { return damageType; }
    public void setDamageType(String damageType) { this.damageType = damageType; }

    public String getOtherDriverSalutation() { return otherDriverSalutation; }
    public void setOtherDriverSalutation(String otherDriverSalutation) { this.otherDriverSalutation = otherDriverSalutation; }

    public String getOtherDriverName() { return otherDriverName; }
    public void setOtherDriverName(String otherDriverName) { this.otherDriverName = otherDriverName; }

    public String getOtherDriverSurName() { return otherDriverSurName; }
    public void setOtherDriverSurName(String otherDriverSurName) { this.otherDriverSurName = otherDriverSurName; }

    public String getOtherDriverStreetName() { return otherDriverStreetName; }
    public void setOtherDriverStreetName(String otherDriverStreetName) { this.otherDriverStreetName = otherDriverStreetName; }

    public String getOtherDriverHouseNumber() { return otherDriverHouseNumber; }
    public void setOtherDriverHouseNumber(String otherDriverHouseNumber) { this.otherDriverHouseNumber = otherDriverHouseNumber; }

    public String getOtherDriverPostalCode() { return otherDriverPostalCode; }
    public void setOtherDriverPostalCode(String otherDriverPostalCode) { this.otherDriverPostalCode = otherDriverPostalCode; }

    public String getOtherDriverCity() { return otherDriverCity; }
    public void setOtherDriverCity(String otherDriverCity) { this.otherDriverCity = otherDriverCity; }

    public String getOtherDriverTelephone() { return otherDriverTelephone; }
    public void setOtherDriverTelephone(String otherDriverTelephone) { this.otherDriverTelephone = otherDriverTelephone; }

    public String getOtherDriverEmail() { return otherDriverEmail; }
    public void setOtherDriverEmail(String otherDriverEmail) { this.otherDriverEmail = otherDriverEmail; }

    public String getOtherDriverDriverLicense() { return otherDriverDriverLicense; }
    public void setOtherDriverDriverLicense(String otherDriverDriverLicense) { this.otherDriverDriverLicense = otherDriverDriverLicense; }

    public String getOtherDriverLicenseIssuingAuthority() { return otherDriverLicenseIssuingAuthority; }
    public void setOtherDriverLicenseIssuingAuthority(String otherDriverLicenseIssuingAuthority) { this.otherDriverLicenseIssuingAuthority = otherDriverLicenseIssuingAuthority; }

    public List<String> getOtherDriverDamagedParts() { return otherDriverDamagedParts; }
    public void setOtherDriverDamagedParts(List<String> otherDriverDamagedParts) { this.otherDriverDamagedParts = otherDriverDamagedParts; }

    public String getOtherDamageDescription() { return otherDamageDescription; }
    public void setOtherDamageDescription(String otherDamageDescription) { this.otherDamageDescription = otherDamageDescription; }

    public String getOtherAdditionalComments() { return otherAdditionalComments; }
    public void setOtherAdditionalComments(String otherAdditionalComments) { this.otherAdditionalComments = otherAdditionalComments; }

    public TriState getOtherVehicleOperational() { return otherVehicleOperational; }
    public void setOtherVehicleOperational(TriState otherVehicleOperational) { this.otherVehicleOperational = otherVehicleOperational; }

    public String getOtherDamageType() { return otherDamageType; }
    public void setOtherDamageType(String otherDamageType) { this.otherDamageType = otherDamageType; }
}
