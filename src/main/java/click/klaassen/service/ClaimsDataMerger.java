package click.klaassen.service;

import click.klaassen.claims.model.Claimsdata;
import click.klaassen.claims.model.DamagedImage;
import click.klaassen.claims.model.Person;
import click.klaassen.claims.model.Policyholder;
import click.klaassen.claims.model.VehicleDriver;
import click.klaassen.claims.model.Witness;
import click.klaassen.claims.model.enums.DamagedPart;
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
        merged.setLanguage(extracted.getLanguage() != null ? extracted.getLanguage() : current.getLanguage());
        merged.setAccidentDate(mergeScalar(current.getAccidentDate(), extracted.getAccidentDate()));
        merged.setAccidentTime(mergeScalar(current.getAccidentTime(), extracted.getAccidentTime()));
        merged.setAccidentPostalCode(mergeScalar(current.getAccidentPostalCode(), extracted.getAccidentPostalCode()));
        merged.setAccidentCity(mergeScalar(current.getAccidentCity(), extracted.getAccidentCity()));
        merged.setAccidentStreetName(mergeScalar(current.getAccidentStreetName(), extracted.getAccidentStreetName()));
        merged.setAccidentStreetNumber(mergeScalar(current.getAccidentStreetNumber(), extracted.getAccidentStreetNumber()));
        merged.setAccidentDescription(mergeScalar(current.getAccidentDescription(), extracted.getAccidentDescription()));
        merged.setAccidentPoliceNumber(mergeScalar(current.getAccidentPoliceNumber(), extracted.getAccidentPoliceNumber()));
        merged.setHasVehicleDamage(extracted.getHasVehicleDamage() != null ? extracted.getHasVehicleDamage() : current.getHasVehicleDamage());
        merged.setVehicleDamageDescription(mergeScalar(current.getVehicleDamageDescription(), extracted.getVehicleDamageDescription()));
        merged.setInjuredPerson(extracted.getInjuredPerson() != null ? extracted.getInjuredPerson() : current.getInjuredPerson());
        merged.setInjuredPersonNumber(mergeScalar(current.getInjuredPersonNumber(), extracted.getInjuredPersonNumber()));
        merged.setWitnessExists(extracted.getWitnessExists() != null ? extracted.getWitnessExists() : current.getWitnessExists());
        merged.setWitnessCount(mergeScalar(current.getWitnessCount(), extracted.getWitnessCount()));
        merged.setWitness(mergeWitnesses(current.getWitness(), extracted.getWitness()));
        merged.setVehicleDriver(mergeVehicleDriver(current.getVehicleDriver(), extracted.getVehicleDriver()));
        merged.setOtherVehicleDriver(mergeVehicleDriver(current.getOtherVehicleDriver(), extracted.getOtherVehicleDriver()));
        merged.setPolicyholder(mergePolicyholder(current.getPolicyholder(), extracted.getPolicyholder()));
        merged.setOtherPolicyholder(mergePolicyholder(current.getOtherPolicyholder(), extracted.getOtherPolicyholder()));

        return merged;
    }

    private String mergeScalar(String current, String extracted) {
        return extracted != null ? extracted : current;
    }

    private List<Witness> mergeWitnesses(List<Witness> current, List<Witness> extracted) {
        if (extracted == null || extracted.isEmpty()) {
            return current;
        }
        List<Witness> result = current != null ? new ArrayList<>(current) : new ArrayList<>();
        result.addAll(extracted);
        return result;
    }

    private Person mergePerson(Person current, Person extracted) {
        if (extracted == null) {
            return current;
        }
        if (current == null) {
            return extracted;
        }
        Person merged = new Person();
        merged.setFormOfAddress(extracted.getFormOfAddress() != null ? extracted.getFormOfAddress() : current.getFormOfAddress());
        merged.setTitle(extracted.getTitle() != null ? extracted.getTitle() : current.getTitle());
        merged.setLastName(mergeScalar(current.getLastName(), extracted.getLastName()));
        merged.setFirstName(mergeScalar(current.getFirstName(), extracted.getFirstName()));
        merged.setPostalCode(mergeScalar(current.getPostalCode(), extracted.getPostalCode()));
        merged.setCity(mergeScalar(current.getCity(), extracted.getCity()));
        merged.setStreetName(mergeScalar(current.getStreetName(), extracted.getStreetName()));
        merged.setStreetNumber(mergeScalar(current.getStreetNumber(), extracted.getStreetNumber()));
        merged.setPhoneNumber(mergeScalar(current.getPhoneNumber(), extracted.getPhoneNumber()));
        merged.setEmailAddress(mergeScalar(current.getEmailAddress(), extracted.getEmailAddress()));
        return merged;
    }

    private Policyholder mergePolicyholder(Policyholder current, Policyholder extracted) {
        if (extracted == null) {
            return current;
        }
        if (current == null) {
            return extracted;
        }
        Policyholder merged = new Policyholder();
        merged.setPersonalInformation(mergePerson(current.getPersonalInformation(), extracted.getPersonalInformation()));
        merged.setInputTaxDeduction(extracted.getInputTaxDeduction() != null ? extracted.getInputTaxDeduction() : current.getInputTaxDeduction());
        merged.setVehicleMake(mergeScalar(current.getVehicleMake(), extracted.getVehicleMake()));
        merged.setVehicleType(mergeScalar(current.getVehicleType(), extracted.getVehicleType()));
        merged.setVehicleReg(mergeScalar(current.getVehicleReg(), extracted.getVehicleReg()));
        merged.setInsuranceCompany(mergeScalar(current.getInsuranceCompany(), extracted.getInsuranceCompany()));
        merged.setPolicyNumber(mergeScalar(current.getPolicyNumber(), extracted.getPolicyNumber()));
        merged.setVin(mergeScalar(current.getVin(), extracted.getVin()));
        merged.setCurrentMileage(extracted.getCurrentMileage() != null ? extracted.getCurrentMileage() : current.getCurrentMileage());
        merged.setGreencardNumber(mergeScalar(current.getGreencardNumber(), extracted.getGreencardNumber()));
        merged.setGreencardExpirydate(mergeScalar(current.getGreencardExpirydate(), extracted.getGreencardExpirydate()));
        merged.setComprehensiveInsurance(extracted.getComprehensiveInsurance() != null ? extracted.getComprehensiveInsurance() : current.getComprehensiveInsurance());
        return merged;
    }

    private List<DamagedImage> mergeDamagedImages(List<DamagedImage> current, List<DamagedImage> extracted) {
        if (extracted == null || extracted.isEmpty()) {
            return current;
        }
        List<DamagedImage> result = current != null ? new ArrayList<>(current) : new ArrayList<>();
        result.addAll(extracted);
        return result;
    }

    private List<DamagedPart> mergeDamagedParts(List<DamagedPart> current, List<DamagedPart> extracted) {
        if (extracted == null || extracted.isEmpty()) {
            return current;
        }
        Set<DamagedPart> parts = new LinkedHashSet<>();
        if (current != null) {
            parts.addAll(current);
        }
        parts.addAll(extracted);
        return new ArrayList<>(parts);
    }

    private VehicleDriver mergeVehicleDriver(VehicleDriver current, VehicleDriver extracted) {
        if (extracted == null) {
            return current;
        }
        if (current == null) {
            return extracted;
        }
        VehicleDriver merged = new VehicleDriver();
        merged.setPersonalInformation(mergePerson(current.getPersonalInformation(), extracted.getPersonalInformation()));
        merged.setDriverLicensenumber(mergeScalar(current.getDriverLicensenumber(), extracted.getDriverLicensenumber()));
        merged.setLicenseIssuedBy(mergeScalar(current.getLicenseIssuedBy(), extracted.getLicenseIssuedBy()));
        merged.setDamagedCarImages(mergeDamagedImages(current.getDamagedCarImages(), extracted.getDamagedCarImages()));
        merged.setDamagedWindowImages(mergeDamagedImages(current.getDamagedWindowImages(), extracted.getDamagedWindowImages()));
        merged.setDriverDamagedpartsGraphic(mergeDamagedParts(current.getDriverDamagedpartsGraphic(), extracted.getDriverDamagedpartsGraphic()));
        merged.setDriverVisibleDamage(mergeScalar(current.getDriverVisibleDamage(), extracted.getDriverVisibleDamage()));
        merged.setDriverComments(mergeScalar(current.getDriverComments(), extracted.getDriverComments()));
        merged.setVehicleDrivingAbility(extracted.getVehicleDrivingAbility() != null ? extracted.getVehicleDrivingAbility() : current.getVehicleDrivingAbility());
        merged.setDamageCausedBy(extracted.getDamageCausedBy() != null ? extracted.getDamageCausedBy() : current.getDamageCausedBy());
        merged.setTypeOfWildlife(mergeScalar(current.getTypeOfWildlife(), extracted.getTypeOfWildlife()));
        merged.setCertificateForWildlife(mergeScalar(current.getCertificateForWildlife(), extracted.getCertificateForWildlife()));
        merged.setGarageLocation(mergeScalar(current.getGarageLocation(), extracted.getGarageLocation()));
        return merged;
    }
}
