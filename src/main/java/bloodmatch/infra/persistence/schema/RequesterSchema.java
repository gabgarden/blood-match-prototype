package bloodmatch.infra.persistence.schema;

import bloodmatch.domain.party.Party;
import bloodmatch.domain.repositories.PartyRepositoryInterface;
import bloodmatch.domain.roles.requester.Requester;
import bloodmatch.domain.shared.entity.Observer;
import bloodmatch.domain.shared.valueObjects.DomainID;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Transient;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.UUID;

@Document(collection = "requesters")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class RequesterSchema implements Observer {

  @Id
  private String id;
  private String partyId;

  @Transient
  private Requester subject;

  public RequesterSchema(Requester subject) {
    if (subject == null)
      throw new IllegalArgumentException("Requester cannot be null");

    this.subject = subject;
    this.subject.addObserver(this);
    this.update();
  }

  public Requester toDomain(PartyRepositoryInterface partyRepository) {
    if (subject == null) {
      DomainID persistedPartyId = new DomainID(UUID.fromString(this.partyId));
      Party party = partyRepository.findById(persistedPartyId)
          .orElseThrow(() -> new IllegalArgumentException("Party not found"));
      this.subject = new Requester(party);
      this.subject.addObserver(this);
    }

    return this.subject;
  }

  @Override
  public void update() {
    this.id = subject.getId().getValue().toString();
    this.partyId = subject.getParty().getId().getValue().toString();
  }
}
