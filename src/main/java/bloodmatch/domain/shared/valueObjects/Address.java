package bloodmatch.domain.shared.valueObjects;

import java.util.Objects;

public class Address {

    private final String street;
    private final String city;
    private final String state;
    private final String zipCode;

    public Address(
            String street,
            String city,
            String state,
            String zipCode) {

        if (street == null || city == null || state == null)
            throw new IllegalArgumentException("Address fields cannot be null");

        this.street = street;
        this.city = city;
        this.state = state;
        this.zipCode = zipCode;
    }

    public String getStreet() {
        return street;
    }

    public String getCity() {
        return city;
    }

    public String getState() {
        return state;
    }

    public String getZipCode() {
        return zipCode;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (!(o instanceof Address))
            return false;
        Address address = (Address) o;
        return street.equals(address.street) &&
                city.equals(address.city) &&
                state.equals(address.state) &&
                Objects.equals(zipCode, address.zipCode);
    }

    @Override
    public int hashCode() {
        return Objects.hash(street, city, state, zipCode);
    }

}