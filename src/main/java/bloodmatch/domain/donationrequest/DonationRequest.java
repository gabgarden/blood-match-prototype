package bloodmatch.domain.donationrequest;

import bloodmatch.domain.roles.organization.bloodcenter.BloodCenter;
import bloodmatch.domain.roles.person.donor.Donor;
import bloodmatch.domain.roles.requester.Requester;
import bloodmatch.domain.shared.entity.DomainObject;
import bloodmatch.domain.shared.valueObjects.BloodType;
import bloodmatch.domain.shared.valueObjects.DomainID;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class DonationRequest extends DomainObject {

  private Requester requester;
  private BloodCenter bloodCenter;
  private BloodType bloodTypeNeeded;
  private LocalDate dateRequested;
  private LocalDate dateLimit;
  private boolean active;
  private List<Donor> acceptedDonors = new ArrayList<>();
  private Urgency urgency;

  private DonationRequest(
      Requester requester,
      BloodCenter bloodCenter,
      BloodType bloodTypeNeeded,
      LocalDate dateLimit,
      LocalDate currentDate,
      Urgency urgency) {
    this.id = DomainID.generate();
    this.requester = requester;
    this.bloodCenter = bloodCenter;
    this.bloodTypeNeeded = bloodTypeNeeded;
    this.dateRequested = currentDate;
    this.dateLimit = dateLimit;
    this.active = true;
    this.urgency = urgency;
  }

  public static DonationRequest create(
      Requester requester,
      BloodCenter bloodCenter,
      BloodType bloodTypeNeeded,
      LocalDate dateLimit,
      Urgency urgency) {
    return create(
        requester,
        bloodCenter,
        bloodTypeNeeded,
        dateLimit,
        LocalDate.now(),
        urgency);
  }

  public static DonationRequest create(
      Requester requester,
      BloodCenter bloodCenter,
      BloodType bloodTypeNeeded,
      LocalDate dateLimit,
      LocalDate currentDate,
      Urgency urgency) {
    if (requester == null)
      throw new IllegalArgumentException("Requester cannot be null");
    if (bloodCenter == null)
      throw new IllegalArgumentException("Blood center cannot be null");
    if (bloodTypeNeeded == null)
      throw new IllegalArgumentException("Blood type cannot be null");
    if (dateLimit == null)
      throw new IllegalArgumentException("Limit date cannot be null");
    if (currentDate == null)
      throw new IllegalArgumentException("Current date cannot be null");
    if (dateLimit.isBefore(currentDate))
      throw new IllegalArgumentException("Limit date invalid");
    return new DonationRequest(
        requester,
        bloodCenter,
        bloodTypeNeeded,
        dateLimit,
        currentDate,
        urgency);
  }

  public static DonationRequest reconstitute(
      DomainID id,
      Requester requester,
      BloodCenter bloodCenter,
      BloodType bloodTypeNeeded,
      LocalDate dateRequested,
      LocalDate dateLimit,
      boolean active,
      List<Donor> acceptedDonors,
      Urgency urgency) {

    if (id == null)
      throw new IllegalArgumentException("Id cannot be null");
    if (requester == null)
      throw new IllegalArgumentException("Requester cannot be null");
    if (bloodCenter == null)
      throw new IllegalArgumentException("Blood center cannot be null");
    if (bloodTypeNeeded == null)
      throw new IllegalArgumentException("Blood type cannot be null");
    if (dateRequested == null)
      throw new IllegalArgumentException("Requested date cannot be null");
    if (dateLimit == null)
      throw new IllegalArgumentException("Limit date cannot be null");
    if (acceptedDonors == null)
      throw new IllegalArgumentException("Accepted donors cannot be null");

    DonationRequest request = new DonationRequest(
        requester,
        bloodCenter,
        bloodTypeNeeded,
        dateLimit,
        dateRequested,
        urgency);

    request.setId(id);
    request.dateRequested = dateRequested;
    request.active = active;
    request.acceptedDonors = new ArrayList<>(acceptedDonors);
    return request;
  }

  public void close() {
    if (!active)
      throw new IllegalStateException("Request already closed");

    this.active = false;
  }

  public boolean isActive() {
    return active;
  }

  public boolean isExpired() {
    return isExpired(LocalDate.now());
  }

  public boolean isExpired(LocalDate currentDate) {
    if (currentDate == null)
      throw new IllegalArgumentException("Current date cannot be null");

    return currentDate.isAfter(dateLimit);
  }

  public boolean canBeFulfilledBy(BloodType candidateBloodType) {
    return canBeFulfilledBy(candidateBloodType, LocalDate.now());
  }

  public boolean canBeFulfilledBy(
      BloodType candidateBloodType,
      LocalDate currentDate) {

    if (candidateBloodType == null)
      throw new IllegalArgumentException("Candidate blood type cannot be null");

    if (currentDate == null)
      throw new IllegalArgumentException("Current date cannot be null");

    if (!active)
      return false;

    if (isExpired(currentDate))
      return false;

    return candidateBloodType.canDonateTo(bloodTypeNeeded);
  }

  public void acceptBy(Donor donor) {
    acceptBy(donor, LocalDate.now());
  }

  public void acceptBy(Donor donor, LocalDate currentDate) {

    if (donor == null)
      throw new IllegalArgumentException("Donor cannot be null");

    if (currentDate == null)
      throw new IllegalArgumentException("Current date cannot be null");

    if (!isActive())
      throw new IllegalStateException("Request is not active");

    if (!canBeFulfilledBy(donor.getBloodType(), currentDate))
      throw new IllegalArgumentException("Donor blood type incompatible");

    if (!donor.isEligibleToDonate(currentDate))
      throw new IllegalStateException("Donor not eligible to donate");

    if (acceptedDonors.contains(donor))
      throw new IllegalStateException("Donor already accepted this request");

    acceptedDonors.add(donor);
  }

  public BloodType getBloodTypeNeeded() {
    return bloodTypeNeeded;
  }

  public BloodCenter getBloodCenter() {
    return bloodCenter;
  }

  public LocalDate getDateRequested() {
    return dateRequested;
  }

  public LocalDate getDateLimit() {
    return dateLimit;
  }

  public Requester getRequester() {
    return requester;
  }

  public List<Donor> getAcceptedDonors() {
    return Collections.unmodifiableList(acceptedDonors);
  }

  public Urgency getUrgency() {
    return urgency;
  }

  
}