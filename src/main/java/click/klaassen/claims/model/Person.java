package click.klaassen.claims.model;

import click.klaassen.claims.model.enums.AcademicTitle;
import click.klaassen.claims.model.enums.FormOfAddress;
import com.fasterxml.jackson.annotation.JsonInclude;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class Person {

    private FormOfAddress formOfAddress;
    private AcademicTitle title;
    private String lastName;
    private String firstName;
    private String postalCode;
    private String city;
    private String streetName;
    private String streetNumber;
    private String phoneNumber;
    private String emailAddress;

    public FormOfAddress getFormOfAddress() {
        return formOfAddress;
    }

    public void setFormOfAddress(FormOfAddress formOfAddress) {
        this.formOfAddress = formOfAddress;
    }

    public AcademicTitle getTitle() {
        return title;
    }

    public void setTitle(AcademicTitle title) {
        this.title = title;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getPostalCode() {
        return postalCode;
    }

    public void setPostalCode(String postalCode) {
        this.postalCode = postalCode;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getStreetName() {
        return streetName;
    }

    public void setStreetName(String streetName) {
        this.streetName = streetName;
    }

    public String getStreetNumber() {
        return streetNumber;
    }

    public void setStreetNumber(String streetNumber) {
        this.streetNumber = streetNumber;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getEmailAddress() {
        return emailAddress;
    }

    public void setEmailAddress(String emailAddress) {
        this.emailAddress = emailAddress;
    }
}
