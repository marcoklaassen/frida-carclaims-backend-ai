package click.klaassen.service;

import click.klaassen.claims.model.Claimsdata;
import click.klaassen.claims.model.WitnessDetails;
import click.klaassen.claims.model.enums.Language;
import click.klaassen.claims.model.enums.TriState;
import jakarta.enterprise.context.ApplicationScoped;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

@ApplicationScoped
public class ClaimsDataMerger {

    public Claimsdata merge(Claimsdata current, Claimsdata extracted) {
        if (extracted == null) {
            return current;
        }
        if (current == null) {
            return extracted;
        }

        Claimsdata merged = new Claimsdata();

        // --- General ---
        merged.setLanguage(mergeEnum(current.getLanguage(), extracted.getLanguage()));

        // --- Accident ---
        merged.setAccidentDate(mergeScalar(current.getAccidentDate(), extracted.getAccidentDate()));
        merged.setAccidentTime(mergeScalar(current.getAccidentTime(), extracted.getAccidentTime()));
        merged.setAccidentPostalCode(mergeScalar(current.getAccidentPostalCode(), extracted.getAccidentPostalCode()));
        merged.setAccidentCity(mergeScalar(current.getAccidentCity(), extracted.getAccidentCity()));
        merged.setAccidentStreetName(mergeScalar(current.getAccidentStreetName(), extracted.getAccidentStreetName()));
        merged.setAccidentHouseNumber(mergeScalar(current.getAccidentHouseNumber(), extracted.getAccidentHouseNumber()));
        merged.setAccidentDetails(mergeScalar(current.getAccidentDetails(), extracted.getAccidentDetails()));
        merged.setAccidentReportNumber(mergeScalar(current.getAccidentReportNumber(), extracted.getAccidentReportNumber()));

        // --- Miscellaneous damages ---
        merged.setMiscellaneousDamages(mergeEnum(current.getMiscellaneousDamages(), extracted.getMiscellaneousDamages()));
        merged.setMiscellaneousDamageDescription(mergeScalar(current.getMiscellaneousDamageDescription(), extracted.getMiscellaneousDamageDescription()));

        // --- Injured ---
        merged.setHasInjured(mergeEnum(current.getHasInjured(), extracted.getHasInjured()));
        merged.setInjuredCount(mergeScalar(current.getInjuredCount(), extracted.getInjuredCount()));

        // --- Witnesses ---
        merged.setHasWitnesses(mergeEnum(current.getHasWitnesses(), extracted.getHasWitnesses()));
        merged.setWitnessesCount(mergeScalar(current.getWitnessesCount(), extracted.getWitnessesCount()));
        merged.setWitnesses(mergeWitnesses(current.getWitnesses(), extracted.getWitnesses()));

        // --- Insurance holder A ---
        merged.setInsuranceHolderSalutation(mergeScalar(current.getInsuranceHolderSalutation(), extracted.getInsuranceHolderSalutation()));
        merged.setInsuranceHolderTitle(mergeScalar(current.getInsuranceHolderTitle(), extracted.getInsuranceHolderTitle()));
        merged.setInsuranceHolderName(mergeScalar(current.getInsuranceHolderName(), extracted.getInsuranceHolderName()));
        merged.setInsuranceHolderSurName(mergeScalar(current.getInsuranceHolderSurName(), extracted.getInsuranceHolderSurName()));
        merged.setInsuranceHolderStreetName(mergeScalar(current.getInsuranceHolderStreetName(), extracted.getInsuranceHolderStreetName()));
        merged.setInsuranceHolderHouseNumber(mergeScalar(current.getInsuranceHolderHouseNumber(), extracted.getInsuranceHolderHouseNumber()));
        merged.setInsuranceHolderPostalCode(mergeScalar(current.getInsuranceHolderPostalCode(), extracted.getInsuranceHolderPostalCode()));
        merged.setInsuranceHolderCity(mergeScalar(current.getInsuranceHolderCity(), extracted.getInsuranceHolderCity()));
        merged.setInsuranceHolderTelephone(mergeScalar(current.getInsuranceHolderTelephone(), extracted.getInsuranceHolderTelephone()));
        merged.setInsuranceHolderEmail(mergeScalar(current.getInsuranceHolderEmail(), extracted.getInsuranceHolderEmail()));
        merged.setVatDeduction(mergeEnum(current.getVatDeduction(), extracted.getVatDeduction()));
        merged.setCarBrand(mergeScalar(current.getCarBrand(), extracted.getCarBrand()));
        merged.setCarModel(mergeScalar(current.getCarModel(), extracted.getCarModel()));
        merged.setLicensePlate(mergeScalar(current.getLicensePlate(), extracted.getLicensePlate()));
        merged.setInsuranceCompany(mergeScalar(current.getInsuranceCompany(), extracted.getInsuranceCompany()));
        merged.setInsuranceNumber(mergeScalar(current.getInsuranceNumber(), extracted.getInsuranceNumber()));
        merged.setChassisNumber(mergeScalar(current.getChassisNumber(), extracted.getChassisNumber()));
        merged.setOdometerReading(mergeScalar(current.getOdometerReading(), extracted.getOdometerReading()));
        merged.setGreenCardNumber(mergeScalar(current.getGreenCardNumber(), extracted.getGreenCardNumber()));
        merged.setValidDateGreenCard(mergeScalar(current.getValidDateGreenCard(), extracted.getValidDateGreenCard()));
        merged.setAllRiskInsurance(mergeEnum(current.getAllRiskInsurance(), extracted.getAllRiskInsurance()));

        // --- Insurance holder B ---
        merged.setOtherInsuranceHolderSalutation(mergeScalar(current.getOtherInsuranceHolderSalutation(), extracted.getOtherInsuranceHolderSalutation()));
        merged.setOtherInsuranceHolderTitle(mergeScalar(current.getOtherInsuranceHolderTitle(), extracted.getOtherInsuranceHolderTitle()));
        merged.setOtherInsuranceHolderName(mergeScalar(current.getOtherInsuranceHolderName(), extracted.getOtherInsuranceHolderName()));
        merged.setOtherInsuranceHolderSurName(mergeScalar(current.getOtherInsuranceHolderSurName(), extracted.getOtherInsuranceHolderSurName()));
        merged.setOtherInsuranceHolderStreetName(mergeScalar(current.getOtherInsuranceHolderStreetName(), extracted.getOtherInsuranceHolderStreetName()));
        merged.setOtherInsuranceHolderHouseNumber(mergeScalar(current.getOtherInsuranceHolderHouseNumber(), extracted.getOtherInsuranceHolderHouseNumber()));
        merged.setOtherInsuranceHolderPostalCode(mergeScalar(current.getOtherInsuranceHolderPostalCode(), extracted.getOtherInsuranceHolderPostalCode()));
        merged.setOtherInsuranceHolderCity(mergeScalar(current.getOtherInsuranceHolderCity(), extracted.getOtherInsuranceHolderCity()));
        merged.setOtherInsuranceHolderTelephone(mergeScalar(current.getOtherInsuranceHolderTelephone(), extracted.getOtherInsuranceHolderTelephone()));
        merged.setOtherInsuranceHolderEmail(mergeScalar(current.getOtherInsuranceHolderEmail(), extracted.getOtherInsuranceHolderEmail()));
        merged.setOtherVatDeduction(mergeEnum(current.getOtherVatDeduction(), extracted.getOtherVatDeduction()));
        merged.setOtherCarBrand(mergeScalar(current.getOtherCarBrand(), extracted.getOtherCarBrand()));
        merged.setOtherCarModel(mergeScalar(current.getOtherCarModel(), extracted.getOtherCarModel()));
        merged.setOtherLicensePlate(mergeScalar(current.getOtherLicensePlate(), extracted.getOtherLicensePlate()));
        merged.setOtherInsuranceCompany(mergeScalar(current.getOtherInsuranceCompany(), extracted.getOtherInsuranceCompany()));
        merged.setOtherInsuranceNumber(mergeScalar(current.getOtherInsuranceNumber(), extracted.getOtherInsuranceNumber()));
        merged.setOtherChassisNumber(mergeScalar(current.getOtherChassisNumber(), extracted.getOtherChassisNumber()));
        merged.setOtherOdometerReading(mergeScalar(current.getOtherOdometerReading(), extracted.getOtherOdometerReading()));
        merged.setOtherGreenCardNumber(mergeScalar(current.getOtherGreenCardNumber(), extracted.getOtherGreenCardNumber()));
        merged.setOtherValidDateGreenCard(mergeScalar(current.getOtherValidDateGreenCard(), extracted.getOtherValidDateGreenCard()));
        merged.setOtherAllRiskInsurance(mergeEnum(current.getOtherAllRiskInsurance(), extracted.getOtherAllRiskInsurance()));

        // --- Driver A ---
        merged.setDriverSalutation(mergeScalar(current.getDriverSalutation(), extracted.getDriverSalutation()));
        merged.setDriverName(mergeScalar(current.getDriverName(), extracted.getDriverName()));
        merged.setDriverSurName(mergeScalar(current.getDriverSurName(), extracted.getDriverSurName()));
        merged.setDriverStreetName(mergeScalar(current.getDriverStreetName(), extracted.getDriverStreetName()));
        merged.setDriverHouseNumber(mergeScalar(current.getDriverHouseNumber(), extracted.getDriverHouseNumber()));
        merged.setDriverPostalCode(mergeScalar(current.getDriverPostalCode(), extracted.getDriverPostalCode()));
        merged.setDriverCity(mergeScalar(current.getDriverCity(), extracted.getDriverCity()));
        merged.setDriverTelephone(mergeScalar(current.getDriverTelephone(), extracted.getDriverTelephone()));
        merged.setDriverEmail(mergeScalar(current.getDriverEmail(), extracted.getDriverEmail()));
        merged.setDriverDriverLicense(mergeScalar(current.getDriverDriverLicense(), extracted.getDriverDriverLicense()));
        merged.setDriverLicenseIssuingAuthority(mergeScalar(current.getDriverLicenseIssuingAuthority(), extracted.getDriverLicenseIssuingAuthority()));
        merged.setDriverDamagedParts(mergeStringList(current.getDriverDamagedParts(), extracted.getDriverDamagedParts()));
        merged.setDamageDescription(mergeScalar(current.getDamageDescription(), extracted.getDamageDescription()));
        merged.setAdditionalComments(mergeScalar(current.getAdditionalComments(), extracted.getAdditionalComments()));
        merged.setVehicleOperational(mergeEnum(current.getVehicleOperational(), extracted.getVehicleOperational()));
        merged.setDamageType(mergeScalar(current.getDamageType(), extracted.getDamageType()));

        // --- Driver B ---
        merged.setOtherDriverSalutation(mergeScalar(current.getOtherDriverSalutation(), extracted.getOtherDriverSalutation()));
        merged.setOtherDriverName(mergeScalar(current.getOtherDriverName(), extracted.getOtherDriverName()));
        merged.setOtherDriverSurName(mergeScalar(current.getOtherDriverSurName(), extracted.getOtherDriverSurName()));
        merged.setOtherDriverStreetName(mergeScalar(current.getOtherDriverStreetName(), extracted.getOtherDriverStreetName()));
        merged.setOtherDriverHouseNumber(mergeScalar(current.getOtherDriverHouseNumber(), extracted.getOtherDriverHouseNumber()));
        merged.setOtherDriverPostalCode(mergeScalar(current.getOtherDriverPostalCode(), extracted.getOtherDriverPostalCode()));
        merged.setOtherDriverCity(mergeScalar(current.getOtherDriverCity(), extracted.getOtherDriverCity()));
        merged.setOtherDriverTelephone(mergeScalar(current.getOtherDriverTelephone(), extracted.getOtherDriverTelephone()));
        merged.setOtherDriverEmail(mergeScalar(current.getOtherDriverEmail(), extracted.getOtherDriverEmail()));
        merged.setOtherDriverDriverLicense(mergeScalar(current.getOtherDriverDriverLicense(), extracted.getOtherDriverDriverLicense()));
        merged.setOtherDriverLicenseIssuingAuthority(mergeScalar(current.getOtherDriverLicenseIssuingAuthority(), extracted.getOtherDriverLicenseIssuingAuthority()));
        merged.setOtherDriverDamagedParts(mergeStringList(current.getOtherDriverDamagedParts(), extracted.getOtherDriverDamagedParts()));
        merged.setOtherDamageDescription(mergeScalar(current.getOtherDamageDescription(), extracted.getOtherDamageDescription()));
        merged.setOtherAdditionalComments(mergeScalar(current.getOtherAdditionalComments(), extracted.getOtherAdditionalComments()));
        merged.setOtherVehicleOperational(mergeEnum(current.getOtherVehicleOperational(), extracted.getOtherVehicleOperational()));
        merged.setOtherDamageType(mergeScalar(current.getOtherDamageType(), extracted.getOtherDamageType()));

        return merged;
    }

    private String mergeScalar(String current, String extracted) {
        return extracted != null ? extracted : current;
    }

    private <E extends Enum<E>> E mergeEnum(E current, E extracted) {
        return extracted != null ? extracted : current;
    }

    private List<WitnessDetails> mergeWitnesses(List<WitnessDetails> current, List<WitnessDetails> extracted) {
        if (extracted == null || extracted.isEmpty()) {
            return current;
        }
        List<WitnessDetails> result = current != null ? new ArrayList<>(current) : new ArrayList<>();
        result.addAll(extracted);
        return result;
    }

    private List<String> mergeStringList(List<String> current, List<String> extracted) {
        if (extracted == null || extracted.isEmpty()) {
            return current;
        }
        Set<String> parts = new LinkedHashSet<>();
        if (current != null) {
            parts.addAll(current);
        }
        parts.addAll(extracted);
        return new ArrayList<>(parts);
    }
}
