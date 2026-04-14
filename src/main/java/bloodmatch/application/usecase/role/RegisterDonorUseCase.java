package bloodmatch.application.usecase.role;

import bloodmatch.domain.party.Person;
import bloodmatch.domain.repositories.DonorRepositoryInterface;
import bloodmatch.domain.repositories.PersonRepositoryInterface;
import bloodmatch.domain.roles.person.donor.Donor;
import bloodmatch.domain.shared.valueObjects.BloodType;
import bloodmatch.domain.shared.valueObjects.DomainID;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class RegisterDonorUseCase {

  private final DonorRepositoryInterface donorRepository;
  private final PersonRepositoryInterface personRepository;

  public RegisterDonorUseCase(
      DonorRepositoryInterface donorRepository,
      PersonRepositoryInterface personRepository) {

    if (donorRepository == null)
      throw new IllegalArgumentException("DonorRepository cannot be null");
    if (personRepository == null)
      throw new IllegalArgumentException("PersonRepository cannot be null");

    this.donorRepository = donorRepository;
    this.personRepository = personRepository;
  }

  @Transactional
  public Donor execute(
      DomainID personId,
      BloodType bloodType,
      double weight) {

    if (personId == null)
      throw new IllegalArgumentException("Person id cannot be null");
    if (bloodType == null)
      throw new IllegalArgumentException("Blood type cannot be null");

    Person person = personRepository.findById(personId)
        .orElseThrow(() -> new IllegalArgumentException("Person not found"));

    if (donorRepository.findByPartyId(personId).isPresent())
      throw new IllegalStateException("Donor already registered for person");

    Donor donor = new Donor(person, bloodType, weight);
    donorRepository.save(donor);
    return donor;
  }
}
