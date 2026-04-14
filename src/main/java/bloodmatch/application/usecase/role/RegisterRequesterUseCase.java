package bloodmatch.application.usecase.role;

import bloodmatch.domain.party.Party;
import bloodmatch.domain.repositories.PartyRepositoryInterface;
import bloodmatch.domain.repositories.RequesterRepositoryInterface;
import bloodmatch.domain.roles.requester.Requester;
import bloodmatch.domain.shared.valueObjects.DomainID;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class RegisterRequesterUseCase {

  private final RequesterRepositoryInterface requesterRepository;
  private final PartyRepositoryInterface partyRepository;

  public RegisterRequesterUseCase(
      RequesterRepositoryInterface requesterRepository,
      PartyRepositoryInterface partyRepository) {

    if (requesterRepository == null)
      throw new IllegalArgumentException("RequesterRepository cannot be null");
    if (partyRepository == null)
      throw new IllegalArgumentException("PartyRepository cannot be null");

    this.requesterRepository = requesterRepository;
    this.partyRepository = partyRepository;
  }

  @Transactional
  public Requester execute(DomainID partyId) {

    if (partyId == null)
      throw new IllegalArgumentException("Party id cannot be null");

    Party party = partyRepository.findById(partyId)
        .orElseThrow(() -> new IllegalArgumentException("Party not found"));

    if (requesterRepository.findByPartyId(partyId).isPresent())
      throw new IllegalStateException("Requester already registered for party");

    Requester requester = new Requester(party);
    requesterRepository.save(requester);
    return requester;
  }
}
