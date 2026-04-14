package bloodmatch.infra.persistence.schema;

import bloodmatch.domain.party.Organization;
import bloodmatch.domain.party.Party;
import bloodmatch.domain.party.Person;
import bloodmatch.domain.shared.entity.Observer;
import bloodmatch.domain.shared.valueObjects.CNPJ;
import bloodmatch.domain.shared.valueObjects.CPF;
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

@Document(collection = "parties")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class PartySchema implements Observer {

  public static final String TYPE_PERSON = "PERSON";
  public static final String TYPE_ORGANIZATION = "ORGANIZATION";

  @Id
  private String id;
  private String partyType;
  private String name;
  private String cpf;
  private LocalDate birthDate;
  private String cnpj;

  @Transient
  private Party subject;

  public PartySchema(Party subject) {
    if (subject == null)
      throw new IllegalArgumentException("Party cannot be null");

    this.subject = subject;
    this.subject.addObserver(this);
    this.update();
  }

  public Party toDomain() {
    if (subject == null) {
      DomainID partyId = new DomainID(UUID.fromString(this.id));

      if (TYPE_PERSON.equals(this.partyType)) {
        this.subject = new PersistedPerson(partyId, this.name, new CPF(this.cpf), this.birthDate);
      } else if (TYPE_ORGANIZATION.equals(this.partyType)) {
        this.subject = new PersistedOrganization(partyId, this.name, new CNPJ(this.cnpj));
      } else {
        throw new IllegalStateException("Unsupported party type: " + this.partyType);
      }

      this.subject.addObserver(this);
    }

    return this.subject;
  }

  @Override
  public void update() {
    this.id = subject.getId().getValue().toString();
    this.name = subject.getName();

    if (subject instanceof Person person) {
      this.partyType = TYPE_PERSON;
      this.cpf = person.getCpf().getValue();
      this.birthDate = person.getBirthDate();
      this.cnpj = null;
      return;
    }

    if (subject instanceof Organization organization) {
      this.partyType = TYPE_ORGANIZATION;
      this.cnpj = organization.getCnpj().getValue();
      this.cpf = null;
      this.birthDate = null;
      return;
    }

    throw new IllegalStateException("Unsupported party subtype: " + subject.getClass().getName());
  }

  private static class PersistedPerson extends Person {

    private PersistedPerson(DomainID id, String name, CPF cpf, LocalDate birthDate) {
      super(name, cpf, birthDate);
      setId(id);
    }
  }

  private static class PersistedOrganization extends Organization {

    private PersistedOrganization(DomainID id, String name, CNPJ cnpj) {
      super(name, cnpj);
      setId(id);
    }
  }
}
