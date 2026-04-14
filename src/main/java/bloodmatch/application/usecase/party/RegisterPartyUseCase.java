package bloodmatch.application.usecase.party;

import bloodmatch.domain.party.Organization;
import bloodmatch.domain.party.Person;
import bloodmatch.domain.repositories.PartyRepositoryInterface;
import bloodmatch.domain.repositories.PersonRepositoryInterface;
import bloodmatch.domain.shared.valueObjects.CNPJ;
import bloodmatch.domain.shared.valueObjects.CPF;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;

@Service
public class RegisterPartyUseCase {

  private final PartyRepositoryInterface partyRepository;
  private final PersonRepositoryInterface personRepository;

  public RegisterPartyUseCase(
      PartyRepositoryInterface partyRepository,
      PersonRepositoryInterface personRepository) {

    if (partyRepository == null)
      throw new IllegalArgumentException("PartyRepository cannot be null");
    if (personRepository == null)
      throw new IllegalArgumentException("PersonRepository cannot be null");

    this.partyRepository = partyRepository;
    this.personRepository = personRepository;
  }

  @Transactional
  public Person registerPerson(
      String name,
      String cpf,
      LocalDate birthDate) {

    Person person = new Person(name, new CPF(cpf), birthDate);
    personRepository.save(person);
    return person;
  }

  @Transactional
  public Organization registerOrganization(
      String name,
      String cnpj) {

    Organization organization = new Organization(name, new CNPJ(cnpj));
    partyRepository.save(organization);
    return organization;
  }
}
