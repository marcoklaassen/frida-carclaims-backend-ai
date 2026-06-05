package click.klaassen.claims.model;

import click.klaassen.claims.model.enums.DamageCause;
import click.klaassen.claims.model.enums.DamagedPart;
import click.klaassen.claims.model.enums.TriState;
import com.fasterxml.jackson.annotation.JsonInclude;
import java.util.List;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class VehicleDriver {

    private Person personalInformation;
    private String driverLicensenumber;
    private String licenseIssuedBy;
    private List<DamagedImage> damagedCarImages;
    private List<DamagedImage> damagedWindowImages;
    private List<DamagedPart> driverDamagedpartsGraphic;
    private String driverVisibleDamage;
    private String driverComments;
    private TriState vehicleDrivingAbility;
    private DamageCause damageCausedBy;
    private String typeOfWildlife;
    private String certificateForWildlife;
    private String garageLocation;

    public Person getPersonalInformation() {
        return personalInformation;
    }

    public void setPersonalInformation(Person personalInformation) {
        this.personalInformation = personalInformation;
    }

    public String getDriverLicensenumber() {
        return driverLicensenumber;
    }

    public void setDriverLicensenumber(String driverLicensenumber) {
        this.driverLicensenumber = driverLicensenumber;
    }

    public String getLicenseIssuedBy() {
        return licenseIssuedBy;
    }

    public void setLicenseIssuedBy(String licenseIssuedBy) {
        this.licenseIssuedBy = licenseIssuedBy;
    }

    public List<DamagedImage> getDamagedCarImages() {
        return damagedCarImages;
    }

    public void setDamagedCarImages(List<DamagedImage> damagedCarImages) {
        this.damagedCarImages = damagedCarImages;
    }

    public List<DamagedImage> getDamagedWindowImages() {
        return damagedWindowImages;
    }

    public void setDamagedWindowImages(List<DamagedImage> damagedWindowImages) {
        this.damagedWindowImages = damagedWindowImages;
    }

    public List<DamagedPart> getDriverDamagedpartsGraphic() {
        return driverDamagedpartsGraphic;
    }

    public void setDriverDamagedpartsGraphic(List<DamagedPart> driverDamagedpartsGraphic) {
        this.driverDamagedpartsGraphic = driverDamagedpartsGraphic;
    }

    public String getDriverVisibleDamage() {
        return driverVisibleDamage;
    }

    public void setDriverVisibleDamage(String driverVisibleDamage) {
        this.driverVisibleDamage = driverVisibleDamage;
    }

    public String getDriverComments() {
        return driverComments;
    }

    public void setDriverComments(String driverComments) {
        this.driverComments = driverComments;
    }

    public TriState getVehicleDrivingAbility() {
        return vehicleDrivingAbility;
    }

    public void setVehicleDrivingAbility(TriState vehicleDrivingAbility) {
        this.vehicleDrivingAbility = vehicleDrivingAbility;
    }

    public DamageCause getDamageCausedBy() {
        return damageCausedBy;
    }

    public void setDamageCausedBy(DamageCause damageCausedBy) {
        this.damageCausedBy = damageCausedBy;
    }

    public String getTypeOfWildlife() {
        return typeOfWildlife;
    }

    public void setTypeOfWildlife(String typeOfWildlife) {
        this.typeOfWildlife = typeOfWildlife;
    }

    public String getCertificateForWildlife() {
        return certificateForWildlife;
    }

    public void setCertificateForWildlife(String certificateForWildlife) {
        this.certificateForWildlife = certificateForWildlife;
    }

    public String getGarageLocation() {
        return garageLocation;
    }

    public void setGarageLocation(String garageLocation) {
        this.garageLocation = garageLocation;
    }
}
