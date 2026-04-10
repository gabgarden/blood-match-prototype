package bloodmatch.domain.party;

import bloodmatch.domain.shared.entity.DomainObject;
import bloodmatch.domain.shared.valueObjects.Address;
import bloodmatch.domain.shared.valueObjects.Email;
import bloodmatch.domain.shared.valueObjects.PhoneNumber;

public abstract class Party extends DomainObject {

    protected String name;
    protected Email email;
    protected PhoneNumber phoneNumber;
    protected Address address;

    protected Party(String name) {

        if (name == null || name.isBlank())
            throw new IllegalArgumentException("Name cannot be empty");

        this.name = name;
    }

    public String getName() {
        return name;
    }

    public Address getAddress() {
        return address;
    }

}