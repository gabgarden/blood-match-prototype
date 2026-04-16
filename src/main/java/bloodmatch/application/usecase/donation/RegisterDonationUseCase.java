
package bloodmatch.application.usecase.donation;

import bloodmatch.domain.donation.Donation;
import bloodmatch.domain.donation.DonationFactory;
import bloodmatch.domain.donationrequest.DonationRequest;
import bloodmatch.domain.repositories.DonationRepositoryInterface;
import bloodmatch.domain.repositories.DonationRequestRepositoryInterface;
import bloodmatch.domain.repositories.DonorRepositoryInterface;
import bloodmatch.domain.repositories.PartyRepositoryInterface;
import bloodmatch.domain.party.Organization;
import bloodmatch.domain.roles.organization.bloodcenter.BloodCenter;
import bloodmatch.domain.roles.person.donor.Donor;
import bloodmatch.domain.shared.valueObjects.DomainID;

import java.time.LocalDate;

public class RegisterDonationUseCase {

  private final DonationFactory donationFactory;
  private final DonorRepositoryInterface donorRepository;
  private final PartyRepositoryInterface partyRepository;
  private final DonationRequestRepositoryInterface donationRequestRepository;
  private final DonationRepositoryInterface donationRepository;

  public RegisterDonationUseCase(
      DonationFactory donationFactory,
      DonorRepositoryInterface donorRepository,
      PartyRepositoryInterface partyRepository,
      DonationRequestRepositoryInterface donationRequestRepository,
      DonationRepositoryInterface donationRepository) {

    if (donationFactory == null)
      throw new IllegalArgumentException("DonationFactory cannot be null");
    if (donorRepository == null)
      throw new IllegalArgumentException("DonorRepository cannot be null");
    if (partyRepository == null)
      throw new IllegalArgumentException("PartyRepository cannot be null");
    if (donationRequestRepository == null)
      throw new IllegalArgumentException("DonationRequestRepository cannot be null");
    if (donationRepository == null)
      throw new IllegalArgumentException("DonationRepository cannot be null");

    this.donationFactory = donationFactory;
    this.donorRepository = donorRepository;
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

    Donor donor = donorRepository.findByPartyId(donorId)
      .orElseThrow(() -> new IllegalArgumentException("Donor role not found"));

    Organization organization = partyRepository.findById(bloodCenterId)
      .filter(Organization.class::isInstance)
      .map(Organization.class::cast)
      .orElseThrow(() -> new IllegalArgumentException("Blood center organization not found"));

    BloodCenter bloodCenter = new BloodCenter(organization);

    Donation donation = donationFactory.createExternalDonation(donor, bloodCenter, date);

    //cause donor was mutaded on the factory
    donorRepository.save(donor);


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

    //cause donor was mutaded on the factory
    donorRepository.save(donor);

    donationRepository.save(donation);
    return donation;
  }
}