package bloodmatch.interfaces;

import bloodmatch.domain.roles.person.donor.Donor;
import bloodmatch.domain.shared.valueObjects.DomainID;

import java.util.Optional;

public interface DonorRepositoryInterface {

  Optional<Donor> findByPartyId(DomainID partyId);
}
