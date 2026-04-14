package bloodmatch.application.usecase.role;

import bloodmatch.domain.repositories.DonorRepositoryInterface;
import bloodmatch.domain.roles.person.donor.Donor;
import bloodmatch.domain.shared.valueObjects.BloodType;
import bloodmatch.domain.shared.valueObjects.DomainID;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class UpdateDonorProfileUseCase {

  private final DonorRepositoryInterface donorRepository;

  public UpdateDonorProfileUseCase(DonorRepositoryInterface donorRepository) {
    if (donorRepository == null)
      throw new IllegalArgumentException("DonorRepository cannot be null");

    this.donorRepository = donorRepository;
  }

  @Transactional
  public Donor execute(
      DomainID personId,
      BloodType bloodType,
      double weight) {

    if (personId == null)
      throw new IllegalArgumentException("Person id cannot be null");

    Donor donor = donorRepository.findByPartyId(personId)
        .orElseThrow(() -> new IllegalArgumentException("Donor not found"));

    donor.updateProfile(bloodType, weight);
    donorRepository.save(donor);
    return donor;
  }
}
