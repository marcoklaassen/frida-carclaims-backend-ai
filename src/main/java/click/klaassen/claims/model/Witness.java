package click.klaassen.claims.model;

import com.fasterxml.jackson.annotation.JsonInclude;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class Witness {

    private Person personalInformation;

    public Person getPersonalInformation() {
        return personalInformation;
    }

    public void setPersonalInformation(Person personalInformation) {
        this.personalInformation = personalInformation;
    }
}
