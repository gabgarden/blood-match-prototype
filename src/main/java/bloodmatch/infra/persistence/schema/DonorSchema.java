package bloodmatch.infra.persistence.schema;

import bloodmatch.domain.party.Person;
import bloodmatch.domain.repositories.PersonRepositoryInterface;
import bloodmatch.domain.roles.person.donor.Donor;
import bloodmatch.domain.shared.entity.Observer;
import bloodmatch.domain.shared.valueObjects.BloodType;
import bloodmatch.domain.shared.valueObjects.DomainID;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Transient;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDate;
import java.util.UUID;

@Document(collection = "donors")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class DonorSchema implements Observer {

  @Id
  private String id;
  private String personId;
  private String bloodType;
  private Double weight;
  private LocalDate lastDonationDate;

  @Transient
  private Donor subject;

  public DonorSchema(Donor subject) {
    if (subject == null)
      throw new IllegalArgumentException("Donor cannot be null");

    this.subject = subject;
    this.subject.addObserver(this);
    this.update();
  }

  public Donor toDomain(PersonRepositoryInterface personRepository) {
    if (subject == null) {
      DomainID persistedPersonId = new DomainID(UUID.fromString(this.personId));
      Person person = personRepository.findById(persistedPersonId)
          .orElseThrow(() -> new IllegalArgumentException("Person not found"));

      Donor donor = new Donor(person, BloodType.of(bloodType), weight);
      if (lastDonationDate != null) {
        donor.registerDonation(lastDonationDate, lastDonationDate);
      }

      this.subject = donor;
      this.subject.addObserver(this);
    }

    return this.subject;
  }

  @Override
  public void update() {
    this.id = subject.getId().getValue().toString();
    this.personId = subject.getPerson().getId().getValue().toString();
    this.bloodType = subject.getBloodType().getType();
    this.weight = subject.getWeight();
    this.lastDonationDate = subject.getLastDonationDate();
  }
}
