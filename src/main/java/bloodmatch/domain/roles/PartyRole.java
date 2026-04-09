package bloodmatch.domain.roles;

import bloodmatch.domain.party.Party;

public abstract class PartyRole {

  protected Party party;

  protected PartyRole(Party party) {

    if (party == null)
      throw new IllegalArgumentException("Party cannot be null");

    this.party = party;
  }

  public Party getParty() {
    return party;
  }

}