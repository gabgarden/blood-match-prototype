package bloodmatch.domain.roles.person;

import bloodmatch.domain.roles.PartyRole;
import bloodmatch.domain.party.Person;

public abstract class PersonRole extends PartyRole {

  protected PersonRole(Person person) {
    super(person);
  }

  public Person getPerson() {
    return (Person) party;
  }

}