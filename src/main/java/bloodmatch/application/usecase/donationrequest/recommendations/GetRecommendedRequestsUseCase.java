package bloodmatch.application.usecase.donationrequest.recommendations;

import bloodmatch.domain.donationrequest.DonationRequest;
import bloodmatch.domain.donationrequest.Urgency;
import bloodmatch.domain.repositories.DonationRequestRepositoryInterface;
import bloodmatch.domain.repositories.DonorRepositoryInterface;
import bloodmatch.domain.roles.person.donor.Donor;
import bloodmatch.domain.shared.valueObjects.DomainID;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
public class GetRecommendedRequestsUseCase {

  private final DonorRepositoryInterface donorRepository;
  private final DonationRequestRepositoryInterface donationRequestRepository;

  public GetRecommendedRequestsUseCase(
      DonorRepositoryInterface donorRepository,
      DonationRequestRepositoryInterface donationRequestRepository) {
    this.donorRepository = donorRepository;
    this.donationRequestRepository = donationRequestRepository;
  }

  public List<OutputItem> execute(DomainID donorId) {
    return execute(donorId, LocalDate.now());
  }

  public List<OutputItem> execute(DomainID donorId, LocalDate currentDate) {
    if (donorId == null)
      throw new IllegalArgumentException("Donor id cannot be null");
    if (currentDate == null)
      throw new IllegalArgumentException("Current date cannot be null");

    Donor donor = donorRepository.findByPartyId(donorId)
        .orElseThrow(() -> new IllegalArgumentException("Donor role not found"));

    return donationRequestRepository.findActiveRequests()
        .stream()
      .filter(request -> donor.isEligibleToDonate(currentDate))
      .filter(request -> request.canBeFulfilledBy(donor.getBloodType(), currentDate))
      .filter(request -> !request.getAcceptedDonors().contains(donor))
        .map(this::toOutput)
        // TODO: Add distance-based ranking using database-side geo query when location indexing is available.
        .sorted(java.util.Comparator.comparing(OutputItem::dateLimit))
        .toList();
  }

  private OutputItem toOutput(DonationRequest request) {
    return new OutputItem(
        request.getId().getValue().toString(),
        request.getBloodTypeNeeded().getType(),
        request.getDateLimit(),
        request.getBloodCenter().getOrganization().getName(),
        request.getUrgency());
  };

  public record OutputItem(
      String requestId,
      String bloodTypeNeeded,
      java.time.LocalDate dateLimit,
      String bloodCenterName,
      Urgency urgency) { 
        
      }
}
