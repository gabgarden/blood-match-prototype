
package bloodmatch.application.usecase;

import bloodmatch.domain.donation.Donation;
import bloodmatch.domain.donation.DonationDomainService;
import bloodmatch.domain.donationRequest.DonationRequest;
import bloodmatch.domain.party.Party;
import bloodmatch.domain.roles.organization.bloodcenter.BloodCenter;
import bloodmatch.domain.roles.person.donor.Donor;
import bloodmatch.domain.shared.valueObjects.DomainID;
import bloodmatch.interfaces.DonationRepositoryInterface;
import bloodmatch.interfaces.DonationRequestRepositoryInterface;
import bloodmatch.interfaces.PartyRepositoryInterface;

import java.time.LocalDate;

public class RegisterDonationUseCase {

  private final DonationDomainService donationDomainService;
  private final PartyRepositoryInterface partyRepository;
  private final DonationRequestRepositoryInterface donationRequestRepository;
  private final DonationRepositoryInterface donationRepository;

  public RegisterDonationUseCase(
      DonationDomainService donationDomainService,
      PartyRepositoryInterface partyRepository,
      DonationRequestRepositoryInterface donationRequestRepository,
      DonationRepositoryInterface donationRepository) {

    if (donationDomainService == null)
      throw new IllegalArgumentException("DonationDomainService cannot be null");
    if (partyRepository == null)
      throw new IllegalArgumentException("PartyRepository cannot be null");
    if (donationRequestRepository == null)
      throw new IllegalArgumentException("DonationRequestRepository cannot be null");
    if (donationRepository == null)
      throw new IllegalArgumentException("DonationRepository cannot be null");

    this.donationDomainService = donationDomainService;
    this.partyRepository = partyRepository;
    this.donationRequestRepository = donationRequestRepository;
    this.donationRepository = donationRepository;
  }

  public Donation executeExternal(
      DomainID donorId,
      DomainID bloodCenterId,
      LocalDate date) {

    if (donorId == null)
      throw new IllegalArgumentException("Donor id cannot be null");
    if (bloodCenterId == null)
      throw new IllegalArgumentException("Blood center id cannot be null");
    if (date == null)
      throw new IllegalArgumentException("Date cannot be null");

    Party donorParty = partyRepository.findById(donorId)
        .orElseThrow(() -> new IllegalArgumentException("Donor party not found"));

    Donor donor = donorParty.getRole(Donor.class)
        .orElseThrow(() -> new IllegalArgumentException("Party does not have Donor role"));

    Party bloodCenterParty = partyRepository.findById(bloodCenterId)
        .orElseThrow(() -> new IllegalArgumentException("Blood center party not found"));

    BloodCenter bloodCenter = bloodCenterParty.getRole(BloodCenter.class)
        .orElseThrow(() -> new IllegalArgumentException("Party does not have BloodCenter role"));

    Donation donation = donationDomainService.registerDonation(donor, bloodCenter, date);
    donationRepository.save(donation);
    return donation;
  }

  public Donation executeFromRequest(
      DomainID donorId,
      DomainID requestId,
      LocalDate date) {

    if (donorId == null)
      throw new IllegalArgumentException("Donor id cannot be null");
    if (requestId == null)
      throw new IllegalArgumentException("Request id cannot be null");

    Party donorParty = partyRepository.findById(donorId)
        .orElseThrow(() -> new IllegalArgumentException("Donor party not found"));

    Donor donor = donorParty.getRole(Donor.class)
        .orElseThrow(() -> new IllegalArgumentException("Party does not have Donor role"));

    DonationRequest request = donationRequestRepository.findById(requestId)
        .orElseThrow(() -> new IllegalArgumentException("Donation request not found"));

    Donation donation = donationDomainService.registerDonationFromRequest(donor, request, date);
    donationRepository.save(donation);
    return donation;
  }
}