package click.klaassen.claims.model;

import click.klaassen.claims.model.enums.TriState;
import com.fasterxml.jackson.annotation.JsonInclude;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class Policyholder {

    private Person personalInformation;
    private TriState inputTaxDeduction;
    private String vehicleMake;
    private String vehicleType;
    private String vehicleReg;
    private String insuranceCompany;
    private String policyNumber;
    private String vin;
    private Integer currentMileage;
    private String greencardNumber;
    private String greencardExpirydate;
    private TriState comprehensiveInsurance;

    public Person getPersonalInformation() {
        return personalInformation;
    }

    public void setPersonalInformation(Person personalInformation) {
        this.personalInformation = personalInformation;
    }

    public TriState getInputTaxDeduction() {
        return inputTaxDeduction;
    }

    public void setInputTaxDeduction(TriState inputTaxDeduction) {
        this.inputTaxDeduction = inputTaxDeduction;
    }

    public String getVehicleMake() {
        return vehicleMake;
    }

    public void setVehicleMake(String vehicleMake) {
        this.vehicleMake = vehicleMake;
    }

    public String getVehicleType() {
        return vehicleType;
    }

    public void setVehicleType(String vehicleType) {
        this.vehicleType = vehicleType;
    }

    public String getVehicleReg() {
        return vehicleReg;
    }

    public void setVehicleReg(String vehicleReg) {
        this.vehicleReg = vehicleReg;
    }

    public String getInsuranceCompany() {
        return insuranceCompany;
    }

    public void setInsuranceCompany(String insuranceCompany) {
        this.insuranceCompany = insuranceCompany;
    }

    public String getPolicyNumber() {
        return policyNumber;
    }

    public void setPolicyNumber(String policyNumber) {
        this.policyNumber = policyNumber;
    }

    public String getVin() {
        return vin;
    }

    public void setVin(String vin) {
        this.vin = vin;
    }

    public Integer getCurrentMileage() {
        return currentMileage;
    }

    public void setCurrentMileage(Integer currentMileage) {
        this.currentMileage = currentMileage;
    }

    public String getGreencardNumber() {
        return greencardNumber;
    }

    public void setGreencardNumber(String greencardNumber) {
        this.greencardNumber = greencardNumber;
    }

    public String getGreencardExpirydate() {
        return greencardExpirydate;
    }

    public void setGreencardExpirydate(String greencardExpirydate) {
        this.greencardExpirydate = greencardExpirydate;
    }

    public TriState getComprehensiveInsurance() {
        return comprehensiveInsurance;
    }

    public void setComprehensiveInsurance(TriState comprehensiveInsurance) {
        this.comprehensiveInsurance = comprehensiveInsurance;
    }
}
