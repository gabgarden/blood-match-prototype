package bloodmatch.domain.roles.requester;

import bloodmatch.domain.roles.PartyRole;

import bloodmatch.domain.party.Party;

public class Requester extends PartyRole {

  public Requester(Party party) {
    super(party);
  }

  public boolean isValidRequester() {
    return getParty()
        .getRole(Requester.class)
        .map(role -> role == this)
        .orElse(false);
  }

}