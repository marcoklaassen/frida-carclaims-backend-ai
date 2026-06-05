package click.klaassen.claims.model;

import click.klaassen.claims.model.enums.Language;
import click.klaassen.claims.model.enums.TriState;
import com.fasterxml.jackson.annotation.JsonInclude;
import java.util.List;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class Claimsdata {

    private Language language;
    private String accidentDate;
    private String accidentTime;
    private String accidentPostalCode;
    private String accidentCity;
    private String accidentStreetName;
    private String accidentStreetNumber;
    private String accidentDescription;
    private String accidentPoliceNumber;
    private TriState hasVehicleDamage;
    private String vehicleDamageDescription;
    private TriState injuredPerson;
    private String injuredPersonNumber;
    private TriState witnessExists;
    private String witnessCount;
    private List<Witness> witness;
    private VehicleDriver vehicleDriver;
    private VehicleDriver otherVehicleDriver;
    private Policyholder policyholder;
    private Policyholder otherPolicyholder;

    public Language getLanguage() {
        return language;
    }

    public void setLanguage(Language language) {
        this.language = language;
    }

    public String getAccidentDate() {
        return accidentDate;
    }

    public void setAccidentDate(String accidentDate) {
        this.accidentDate = accidentDate;
    }

    public String getAccidentTime() {
        return accidentTime;
    }

    public void setAccidentTime(String accidentTime) {
        this.accidentTime = accidentTime;
    }

    public String getAccidentPostalCode() {
        return accidentPostalCode;
    }

    public void setAccidentPostalCode(String accidentPostalCode) {
        this.accidentPostalCode = accidentPostalCode;
    }

    public String getAccidentCity() {
        return accidentCity;
    }

    public void setAccidentCity(String accidentCity) {
        this.accidentCity = accidentCity;
    }

    public String getAccidentStreetName() {
        return accidentStreetName;
    }

    public void setAccidentStreetName(String accidentStreetName) {
        this.accidentStreetName = accidentStreetName;
    }

    public String getAccidentStreetNumber() {
        return accidentStreetNumber;
    }

    public void setAccidentStreetNumber(String accidentStreetNumber) {
        this.accidentStreetNumber = accidentStreetNumber;
    }

    public String getAccidentDescription() {
        return accidentDescription;
    }

    public void setAccidentDescription(String accidentDescription) {
        this.accidentDescription = accidentDescription;
    }

    public String getAccidentPoliceNumber() {
        return accidentPoliceNumber;
    }

    public void setAccidentPoliceNumber(String accidentPoliceNumber) {
        this.accidentPoliceNumber = accidentPoliceNumber;
    }

    public TriState getHasVehicleDamage() {
        return hasVehicleDamage;
    }

    public void setHasVehicleDamage(TriState hasVehicleDamage) {
        this.hasVehicleDamage = hasVehicleDamage;
    }

    public String getVehicleDamageDescription() {
        return vehicleDamageDescription;
    }

    public void setVehicleDamageDescription(String vehicleDamageDescription) {
        this.vehicleDamageDescription = vehicleDamageDescription;
    }

    public TriState getInjuredPerson() {
        return injuredPerson;
    }

    public void setInjuredPerson(TriState injuredPerson) {
        this.injuredPerson = injuredPerson;
    }

    public String getInjuredPersonNumber() {
        return injuredPersonNumber;
    }

    public void setInjuredPersonNumber(String injuredPersonNumber) {
        this.injuredPersonNumber = injuredPersonNumber;
    }

    public TriState getWitnessExists() {
        return witnessExists;
    }

    public void setWitnessExists(TriState witnessExists) {
        this.witnessExists = witnessExists;
    }

    public String getWitnessCount() {
        return witnessCount;
    }

    public void setWitnessCount(String witnessCount) {
        this.witnessCount = witnessCount;
    }

    public List<Witness> getWitness() {
        return witness;
    }

    public void setWitness(List<Witness> witness) {
        this.witness = witness;
    }

    public VehicleDriver getVehicleDriver() {
        return vehicleDriver;
    }

    public void setVehicleDriver(VehicleDriver vehicleDriver) {
        this.vehicleDriver = vehicleDriver;
    }

    public VehicleDriver getOtherVehicleDriver() {
        return otherVehicleDriver;
    }

    public void setOtherVehicleDriver(VehicleDriver otherVehicleDriver) {
        this.otherVehicleDriver = otherVehicleDriver;
    }

    public Policyholder getPolicyholder() {
        return policyholder;
    }

    public void setPolicyholder(Policyholder policyholder) {
        this.policyholder = policyholder;
    }

    public Policyholder getOtherPolicyholder() {
        return otherPolicyholder;
    }

    public void setOtherPolicyholder(Policyholder otherPolicyholder) {
        this.otherPolicyholder = otherPolicyholder;
    }
}
