package bloodmatch.interfaces;

import bloodmatch.domain.shared.valueObjects.DomainID;
import bloodmatch.domain.donationRequest.DonationRequest;

import java.util.List;
import java.util.Optional;

public interface DonationRequestRepositoryInterface {

  void save(DonationRequest request);

  Optional<DonationRequest> findById(DomainID id);

  List<DonationRequest> findActiveRequests();

}