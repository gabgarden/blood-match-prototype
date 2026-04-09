package bloodmatch.domain.roles.organization;

import bloodmatch.domain.roles.PartyRole;
import bloodmatch.domain.party.Organization;

public abstract class OrganizationRole extends PartyRole {

  protected OrganizationRole(Organization organization) {
    super(organization);
  }

  public Organization getOrganization() {
    return (Organization) party;
  }

}