
package bloodmatch.application.usecase;

import bloodmatch.domain.donation.Donation;
import bloodmatch.domain.donation.DonationFactory;
import bloodmatch.domain.donationRequest.DonationRequest;
import bloodmatch.domain.roles.organization.bloodcenter.BloodCenter;
import bloodmatch.domain.roles.person.donor.Donor;
import bloodmatch.domain.shared.valueObjects.DomainID;
import bloodmatch.interfaces.BloodCenterRepositoryInterface;
import bloodmatch.interfaces.DonorRepositoryInterface;
import bloodmatch.interfaces.DonationRepositoryInterface;
import bloodmatch.interfaces.DonationRequestRepositoryInterface;

import java.time.LocalDate;

public class RegisterDonationUseCase {

  private final DonationFactory donationFactory;
    private final DonorRepositoryInterface donorRepository;
    private final BloodCenterRepositoryInterface bloodCenterRepository;
  private final DonationRequestRepositoryInterface donationRequestRepository;
  private final DonationRepositoryInterface donationRepository;

  public RegisterDonationUseCase(
      DonationFactory donationFactory,
      DonorRepositoryInterface donorRepository,
      BloodCenterRepositoryInterface bloodCenterRepository,
      DonationRequestRepositoryInterface donationRequestRepository,
      DonationRepositoryInterface donationRepository) {

    if (donationFactory == null)
      throw new IllegalArgumentException("DonationFactory cannot be null");
    if (donorRepository == null)
      throw new IllegalArgumentException("DonorRepository cannot be null");
    if (bloodCenterRepository == null)
      throw new IllegalArgumentException("BloodCenterRepository cannot be null");
    if (donationRequestRepository == null)
      throw new IllegalArgumentException("DonationRequestRepository cannot be null");
    if (donationRepository == null)
      throw new IllegalArgumentException("DonationRepository cannot be null");

    this.donationFactory = donationFactory;
    this.donorRepository = donorRepository;
    this.bloodCenterRepository = bloodCenterRepository;
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

    Donor donor = donorRepository.findByPartyId(donorId)
      .orElseThrow(() -> new IllegalArgumentException("Donor role not found"));

    BloodCenter bloodCenter = bloodCenterRepository.findByPartyId(bloodCenterId)
      .orElseThrow(() -> new IllegalArgumentException("Blood center role not found"));

    Donation donation = donationFactory.createExternalDonation(donor, bloodCenter, date);
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

    Donor donor = donorRepository.findByPartyId(donorId)
      .orElseThrow(() -> new IllegalArgumentException("Donor role not found"));

    DonationRequest request = donationRequestRepository.findById(requestId)
        .orElseThrow(() -> new IllegalArgumentException("Donation request not found"));

    Donation donation = donationFactory.createDonationFromRequest(donor, request, date);
    donationRepository.save(donation);
    return donation;
  }
}