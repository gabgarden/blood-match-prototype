package bloodmatch.application.usecase;

import bloodmatch.domain.donationRequest.DonationRequest;
import bloodmatch.domain.party.Party;
import bloodmatch.domain.roles.organization.bloodcenter.BloodCenter;
import bloodmatch.domain.roles.requester.Requester;
import bloodmatch.domain.shared.valueObjects.BloodType;
import bloodmatch.domain.shared.valueObjects.DomainID;
import bloodmatch.interfaces.DonationRequestRepositoryInterface;
import bloodmatch.interfaces.PartyRepositoryInterface;

import java.time.LocalDate;

public class CreateDonationRequestUseCase {

  private final DonationRequestRepositoryInterface donationRequestRepository;
  private final PartyRepositoryInterface partyRepository;

  public CreateDonationRequestUseCase(DonationRequestRepositoryInterface donationRequestRepository,
      PartyRepositoryInterface partyRepository) {
    if (donationRequestRepository == null) {
      throw new IllegalArgumentException("DonationRequestRepository cannot be null");
    }
    if (partyRepository == null) {
      throw new IllegalArgumentException("PartyRepository cannot be null");
    }
    this.donationRequestRepository = donationRequestRepository;
    this.partyRepository = partyRepository;
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

    Party requesterParty = partyRepository.findById(requesterID)
        .orElseThrow(() -> new IllegalArgumentException("Requester party not found"));

    Requester requester = requesterParty.getRole(Requester.class)
        .orElseThrow(() -> new IllegalArgumentException("Party does not have Requester role"));

    Party bloodCenterParty = partyRepository.findById(bloodCenterID)
        .orElseThrow(() -> new IllegalArgumentException("Blood center party not found"));

    BloodCenter bloodCenter = bloodCenterParty.getRole(BloodCenter.class)
        .orElseThrow(() -> new IllegalArgumentException("Party does not have BloodCenter role"));

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