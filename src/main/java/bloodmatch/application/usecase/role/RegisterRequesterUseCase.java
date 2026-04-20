package bloodmatch.application.usecase.role;

import bloodmatch.domain.party.Party;
import bloodmatch.domain.repositories.PartyRepositoryInterface;
import bloodmatch.domain.repositories.RequesterRepositoryInterface;
import bloodmatch.domain.repositories.UserAccountRepositoryInterface;
import bloodmatch.domain.security.SecurityRole;
import bloodmatch.domain.security.UserAccount;
import bloodmatch.domain.roles.requester.Requester;
import bloodmatch.domain.shared.valueObjects.DomainID;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.Set;

@Service
public class RegisterRequesterUseCase {

  private final RequesterRepositoryInterface requesterRepository;
  private final PartyRepositoryInterface partyRepository;
  private final UserAccountRepositoryInterface userAccountRepository;

  public RegisterRequesterUseCase(
      RequesterRepositoryInterface requesterRepository,
      PartyRepositoryInterface partyRepository,
      UserAccountRepositoryInterface userAccountRepository) {

    if (requesterRepository == null)
      throw new IllegalArgumentException("RequesterRepository cannot be null");
    if (partyRepository == null)
      throw new IllegalArgumentException("PartyRepository cannot be null");
    if (userAccountRepository == null)
      throw new IllegalArgumentException("UserAccountRepository cannot be null");

    this.requesterRepository = requesterRepository;
    this.partyRepository = partyRepository;
    this.userAccountRepository = userAccountRepository;
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
    addRoleToUserAccount(partyId, SecurityRole.REQUESTER);
    return requester;
  }

  private void addRoleToUserAccount(DomainID partyId, SecurityRole role) {
    UserAccount userAccount = userAccountRepository.findByPartyId(partyId)
        .orElseThrow(() -> new IllegalStateException("User account not found for party"));

    Set<SecurityRole> updatedRoles = new HashSet<>(userAccount.getRoles());
    updatedRoles.add(role);
    userAccount.updateRoles(updatedRoles);
    userAccountRepository.save(userAccount);
  }
}
