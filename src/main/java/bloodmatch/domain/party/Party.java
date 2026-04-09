package bloodmatch.domain.party;

import bloodmatch.domain.shared.entity.DomainObject;
import bloodmatch.domain.shared.valueObjects.Address;
import bloodmatch.domain.shared.valueObjects.Email;
import bloodmatch.domain.shared.valueObjects.PhoneNumber;
import bloodmatch.domain.roles.PartyRole;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public abstract class Party extends DomainObject {

    protected String name;
    protected Email email;
    protected PhoneNumber phoneNumber;
    protected Address address;

    private List<PartyRole> roles = new ArrayList<>();

    protected Party(String name) {

        if (name == null || name.isBlank())
            throw new IllegalArgumentException("Name cannot be empty");

        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void addRole(PartyRole role) {

        if (role == null)
            throw new IllegalArgumentException("Role cannot be null");

        roles.add(role);
    }

    // searches the collection of roles for the first element that matches a given
    // type
    // return it as an Optional, otherwise return an empty Optional
    public <T extends PartyRole> Optional<T> getRole(Class<T> roleType) {

        if (roleType == null)
            throw new IllegalArgumentException("Role cannot be null");

        return roles.stream()
                .filter(roleType::isInstance)
                .map(roleType::cast)
                .findFirst();
    }

    public Address getAddress() {
        return address;
    }

}