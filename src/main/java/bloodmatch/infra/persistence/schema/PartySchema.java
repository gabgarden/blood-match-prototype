package bloodmatch.infra.persistence.schema;

import bloodmatch.domain.party.Organization;
import bloodmatch.domain.party.Party;
import bloodmatch.domain.party.Person;
import bloodmatch.domain.shared.valueObjects.CNPJ;
import bloodmatch.domain.shared.valueObjects.CPF;
import bloodmatch.domain.shared.valueObjects.DomainID;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDate;
import java.util.UUID;

@Document(collection = "parties")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class PartySchema {

  public static final String TYPE_PERSON = "PERSON";
  public static final String TYPE_ORGANIZATION = "ORGANIZATION";

  @Id
  private String id;
  private String partyType;
  private String name;
  private String cpf;
  private LocalDate birthDate;
  private String cnpj;

  public PartySchema(Party party) {
    if (party == null)
      throw new IllegalArgumentException("Party cannot be null");

    this.id = party.getId().getValue().toString();
    this.name = party.getName();

    if (party instanceof Person person) {
      this.partyType = TYPE_PERSON;
      this.cpf = person.getCpf().getValue();
      this.birthDate = person.getBirthDate();
      this.cnpj = null;
      return;
    }

    if (party instanceof Organization organization) {
      this.partyType = TYPE_ORGANIZATION;
      this.cnpj = organization.getCnpj().getValue();
      this.cpf = null;
      this.birthDate = null;
      return;
    }

    throw new IllegalStateException("Unsupported party subtype: " + party.getClass().getName());
  }

  public Party toDomain() {
    DomainID partyId = new DomainID(UUID.fromString(this.id));

    if (TYPE_PERSON.equals(this.partyType)) {
      return new PersistedPerson(partyId, this.name, new CPF(this.cpf), this.birthDate);
    }

    if (TYPE_ORGANIZATION.equals(this.partyType)) {
      return new PersistedOrganization(partyId, this.name, new CNPJ(this.cnpj));
    }

    throw new IllegalStateException("Unsupported party type: " + this.partyType);
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
