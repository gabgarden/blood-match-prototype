package bloodmatch.infra.persistence.schema;

import bloodmatch.domain.donationrequest.DonationRequest;
import bloodmatch.domain.repositories.DonorRepositoryInterface;
import bloodmatch.domain.repositories.PartyRepositoryInterface;
import bloodmatch.domain.repositories.RequesterRepositoryInterface;
import bloodmatch.domain.party.Organization;
import bloodmatch.domain.roles.organization.bloodcenter.BloodCenter;
import bloodmatch.domain.roles.person.donor.Donor;
import bloodmatch.domain.roles.requester.Requester;
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
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Document(collection = "donation_requests")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class DonationRequestSchema implements Observer {

  @Id
  private String id;
  private String requesterId;
  private String bloodCenterId;
  private String bloodTypeNeeded;
  private LocalDate dateRequested;
  private LocalDate dateLimit;
  private boolean active;
  private List<String> acceptedDonorsIds;

  @Transient
  private DonationRequest subject;

  public DonationRequestSchema(DonationRequest subject) {
    if (subject == null)
      throw new IllegalArgumentException("DonationRequest cannot be null");

    this.subject = subject;
    this.subject.addObserver(this);
    this.update();
  }

  public DonationRequest toDomain(
      RequesterRepositoryInterface requesterRepository,
      PartyRepositoryInterface partyRepository,
      DonorRepositoryInterface donorRepository) {

    if (subject == null) {
      DomainID requesterId = new DomainID(UUID.fromString(this.requesterId));
      DomainID bloodCenterId = new DomainID(UUID.fromString(this.bloodCenterId));

      Requester requester = requesterRepository.findByPartyId(requesterId)
          .orElseThrow(() -> new IllegalArgumentException("Requester role not found"));

        Organization organization = partyRepository.findById(bloodCenterId)
          .filter(Organization.class::isInstance)
          .map(Organization.class::cast)
          .orElseThrow(() -> new IllegalArgumentException("Blood center organization not found"));

        BloodCenter bloodCenter = new BloodCenter(organization);

      List<Donor> acceptedDonors = new ArrayList<>();
      if (acceptedDonorsIds != null) {
        for (String donorIdValue : acceptedDonorsIds) {
          DomainID donorId = new DomainID(UUID.fromString(donorIdValue));
          Donor donor = donorRepository.findByPartyId(donorId)
              .orElseThrow(() -> new IllegalArgumentException("Donor role not found: " + donorIdValue));
          acceptedDonors.add(donor);
        }
      }

      this.subject = DonationRequest.reconstitute(
          new DomainID(UUID.fromString(this.id)),
          requester,
          bloodCenter,
          BloodType.of(this.bloodTypeNeeded),
          this.dateRequested,
          this.dateLimit,
          this.active,
          acceptedDonors);

      this.subject.addObserver(this);
    }

    return this.subject;
  }

  @Override
  public void update() {
    this.id = subject.getId().getValue().toString();
    this.requesterId = subject.getRequester().getParty().getId().getValue().toString();
    this.bloodCenterId = subject.getBloodCenter().getOrganization().getId().getValue().toString();
    this.bloodTypeNeeded = subject.getBloodTypeNeeded().getType();
    this.dateRequested = subject.getDateRequested();
    this.dateLimit = subject.getDateLimit();
    this.active = subject.isActive();
    this.acceptedDonorsIds = subject.getAcceptedDonors()
        .stream()
        .map(donor -> donor.getPerson().getId().getValue().toString())
        .toList();
  }
}
