package click.klaassen.service;

public record DemoCustomer(
        String licensePlate,
        String salutation,
        String name,
        String surName,
        String streetName,
        String houseNumber,
        String postalCode,
        String city,
        String telephone,
        String email,
        String carBrand,
        String carModel,
        String insuranceCompany,
        String insuranceNumber,
        String chassisNumber,
        boolean allRiskInsurance) {
}
