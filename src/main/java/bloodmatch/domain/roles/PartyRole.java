package bloodmatch.domain.roles;

import bloodmatch.domain.party.Party;

public abstract class PartyRole<T extends Party> {

  protected T party;

  protected PartyRole(T party) {

    if (party == null)
      throw new IllegalArgumentException("Party cannot be null");

    this.party = party;
  }

  public T getParty() {
    return party;
  }

}