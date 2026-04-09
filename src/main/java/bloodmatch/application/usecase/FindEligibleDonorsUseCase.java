package bloodmatch.application.usecase;

import bloodmatch.domain.donationRequest.DonationRequest;
import bloodmatch.domain.matching.DonorMatchingService;
import bloodmatch.domain.party.Party;
import bloodmatch.domain.roles.person.donor.Donor;
import bloodmatch.domain.shared.valueObjects.DomainID;
import bloodmatch.interfaces.DonationRequestRepositoryInterface;
import bloodmatch.interfaces.DonorRecommendationPolicyInterface;
import bloodmatch.interfaces.PartyRepositoryInterface;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class FindEligibleDonorsUseCase {

  private final DonationRequestRepositoryInterface donationRequestRepository;
  private final PartyRepositoryInterface partyRepository;
  private final DonorMatchingService donorMatchingService;
  private final DonorRecommendationPolicyInterface recommendationPolicy;

  public FindEligibleDonorsUseCase(
      DonationRequestRepositoryInterface donationRequestRepository,
      PartyRepositoryInterface partyRepository,
      DonorMatchingService donorMatchingService) {
    this(donationRequestRepository, partyRepository, donorMatchingService, null);
  }

  public FindEligibleDonorsUseCase(
      DonationRequestRepositoryInterface donationRequestRepository,
      PartyRepositoryInterface partyRepository,
      DonorMatchingService donorMatchingService,
      DonorRecommendationPolicyInterface recommendationPolicy) {

    if (donationRequestRepository == null)
      throw new IllegalArgumentException("DonationRequestRepository cannot be null");
    if (partyRepository == null)
      throw new IllegalArgumentException("PartyRepository cannot be null");
    if (donorMatchingService == null)
      throw new IllegalArgumentException("DonorMatchingService cannot be null");

    this.donationRequestRepository = donationRequestRepository;
    this.partyRepository = partyRepository;
    this.donorMatchingService = donorMatchingService;
    this.recommendationPolicy = recommendationPolicy;
  }

  public List<Donor> execute(
      DomainID requestId,
      List<DomainID> donorIds,
      LocalDate currentDate) {

    if (requestId == null)
      throw new IllegalArgumentException("Request id cannot be null");
    if (donorIds == null)
      throw new IllegalArgumentException("Donor ids cannot be null");
    if (currentDate == null)
      throw new IllegalArgumentException("Current date cannot be null");

    DonationRequest request = donationRequestRepository.findById(requestId)
        .orElseThrow(() -> new IllegalArgumentException("Donation request not found"));

    List<Donor> donors = new ArrayList<>();
    for (DomainID donorId : donorIds) {
      if (donorId == null)
        throw new IllegalArgumentException("Donor id cannot be null");

      Party donorParty = partyRepository.findById(donorId)
          .orElseThrow(() -> new IllegalArgumentException("Donor party not found: " + donorId.getValue()));

      Donor donor = donorParty.getRole(Donor.class)
          .orElseThrow(() -> new IllegalArgumentException("Party does not have Donor role: " + donorId.getValue()));

      donors.add(donor);
    }

    List<Donor> eligible = donorMatchingService.findEligibleDonors(request, donors, currentDate);

    if (recommendationPolicy == null)
      return eligible;

    List<Donor> recommended = new ArrayList<>();
    for (Donor donor : eligible) {
      if (recommendationPolicy.isSatisfiedBy(donor, request)) {
        recommended.add(donor);
      }
    }

    return recommended;
  }
}