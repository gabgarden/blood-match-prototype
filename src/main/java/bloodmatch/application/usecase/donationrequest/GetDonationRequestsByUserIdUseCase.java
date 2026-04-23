package bloodmatch.application.usecase.donationrequest;

import bloodmatch.domain.donationrequest.DonationRequest;
import bloodmatch.domain.donationrequest.Urgency;
import bloodmatch.domain.repositories.DonationRequestRepositoryInterface;
import bloodmatch.domain.shared.valueObjects.DomainID;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.Comparator;
import java.util.List;

@Service
public class GetDonationRequestsByUserIdUseCase {

  private final DonationRequestRepositoryInterface donationRequestRepository;

  public GetDonationRequestsByUserIdUseCase(
      DonationRequestRepositoryInterface donationRequestRepository) {
    this.donationRequestRepository = donationRequestRepository;
  }

  public List<OutputItem> execute(DomainID userId) {
    if (userId == null)
      throw new IllegalArgumentException("User id cannot be null");

    return donationRequestRepository.findByRequesterPartyId(userId)
        .stream()
        .sorted(Comparator.comparing(DonationRequest::getDateRequested).reversed())
        .map(this::toOutput)
        .toList();
  }

  private OutputItem toOutput(DonationRequest request) {
    return new OutputItem(
        request.getId().getValue().toString(),
        request.getBloodTypeNeeded().getType(),
        request.getDateRequested(),
        request.getDateLimit(),
        request.isActive(),
        request.getBloodCenter().getOrganization().getName(),
        request.getUrgency());
  }

  public record OutputItem(
      String requestId,
      String bloodTypeNeeded,
      LocalDate dateRequested,
      LocalDate dateLimit,
      boolean active,
      String bloodCenterName,
      Urgency urgency) {
  }
}
