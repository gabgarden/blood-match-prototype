package bloodmatch.domain.repositories;

import bloodmatch.domain.donation.Donation;
import bloodmatch.domain.shared.valueObjects.DomainID;

import java.util.Optional;




public interface DonationRepositoryInterface {

  void save(Donation donation);

  Optional<Donation> findById(DomainID id);
}