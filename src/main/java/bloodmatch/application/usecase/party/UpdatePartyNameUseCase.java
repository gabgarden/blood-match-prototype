package bloodmatch.application.usecase.party;

import bloodmatch.domain.party.Party;
import bloodmatch.domain.repositories.PartyRepositoryInterface;
import bloodmatch.domain.shared.valueObjects.DomainID;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class UpdatePartyNameUseCase {

  private final PartyRepositoryInterface partyRepository;

  public UpdatePartyNameUseCase(PartyRepositoryInterface partyRepository) {
    if (partyRepository == null)
      throw new IllegalArgumentException("PartyRepository cannot be null");

    this.partyRepository = partyRepository;
  }

  @Transactional
  public Party execute(DomainID partyId, String newName) {

    if (partyId == null)
      throw new IllegalArgumentException("Party id cannot be null");

    Party party = partyRepository.findById(partyId)
        .orElseThrow(() -> new IllegalArgumentException("Party not found"));

    party.changeName(newName);
    partyRepository.save(party);
    return party;
  }
}
