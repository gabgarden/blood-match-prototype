package bloodmatch.domain.roles.requester;

import bloodmatch.domain.roles.PartyRole;

import bloodmatch.domain.party.Party;

public class Requester extends PartyRole<Party> {

  public Requester(Party party) {
    super(party);
  }

}