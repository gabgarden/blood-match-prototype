package bloodmatch.application.usecase;

import bloodmatch.domain.donationRequest.DonationRequest;
import bloodmatch.domain.roles.organization.bloodcenter.BloodCenter;
import bloodmatch.domain.roles.requester.Requester;
import bloodmatch.domain.shared.valueObjects.BloodType;
import bloodmatch.domain.shared.valueObjects.DomainID;
import bloodmatch.interfaces.BloodCenterRepositoryInterface;
import bloodmatch.interfaces.DonationRequestRepositoryInterface;
import bloodmatch.interfaces.RequesterRepositoryInterface;

import java.time.LocalDate;

public class CreateDonationRequestUseCase {

  private final DonationRequestRepositoryInterface donationRequestRepository;
  private final RequesterRepositoryInterface requesterRepository;
  private final BloodCenterRepositoryInterface bloodCenterRepository;

  public CreateDonationRequestUseCase(DonationRequestRepositoryInterface donationRequestRepository,
      RequesterRepositoryInterface requesterRepository,
      BloodCenterRepositoryInterface bloodCenterRepository) {
    if (donationRequestRepository == null) {
      throw new IllegalArgumentException("DonationRequestRepository cannot be null");
    }
    if (requesterRepository == null) {
      throw new IllegalArgumentException("RequesterRepository cannot be null");
    }
    if (bloodCenterRepository == null) {
      throw new IllegalArgumentException("BloodCenterRepository cannot be null");
    }
    this.donationRequestRepository = donationRequestRepository;
    this.requesterRepository = requesterRepository;
    this.bloodCenterRepository = bloodCenterRepository;
  }

  public DonationRequest execute(
      DomainID requesterID,
      DomainID bloodCenterID,
      BloodType bloodTypeNeeded,
      LocalDate dateLimit) {
    return execute(
        requesterID,
        bloodCenterID,
        bloodTypeNeeded,
        dateLimit,
        LocalDate.now());
  }

  public DonationRequest execute(
      DomainID requesterID,
      DomainID bloodCenterID,
      BloodType bloodTypeNeeded,
      LocalDate dateLimit,
      LocalDate currentDate) {

    if (requesterID == null)
      throw new IllegalArgumentException("Requester id cannot be null");
    if (bloodCenterID == null)
      throw new IllegalArgumentException("Blood center id cannot be null");
    if (bloodTypeNeeded == null)
      throw new IllegalArgumentException("Blood type needed cannot be null");
    if (dateLimit == null)
      throw new IllegalArgumentException("Date limit cannot be null");
    if (currentDate == null)
      throw new IllegalArgumentException("Current date cannot be null");
    if (dateLimit.isBefore(currentDate))
      throw new IllegalArgumentException("Date limit cannot be before current date");

    Requester requester = requesterRepository.findByPartyId(requesterID)
      .orElseThrow(() -> new IllegalArgumentException("Requester role not found"));

    BloodCenter bloodCenter = bloodCenterRepository.findByPartyId(bloodCenterID)
      .orElseThrow(() -> new IllegalArgumentException("Blood center role not found"));

    DonationRequest request = DonationRequest.create(
        requester,
        bloodCenter,
        bloodTypeNeeded,
        dateLimit,
        currentDate);

    donationRequestRepository.save(request);
    return request;
  }
}