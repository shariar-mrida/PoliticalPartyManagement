package model;
public class Address {
    private District district;

    public Address(District district) {
        this.district = district;
    }

    public District getDistrict() {
        return district;
    }

    public Division getDivision() {
        return district.getDivision();
    }

    @Override
    public String toString() {
        return "Division: " + getDivision() + ", District: " + district;
    }
}