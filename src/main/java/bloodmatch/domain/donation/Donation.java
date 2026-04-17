package bloodmatch.domain.donation;

import bloodmatch.domain.donationrequest.DonationRequest;
import bloodmatch.domain.roles.organization.bloodcenter.BloodCenter;
import bloodmatch.domain.roles.person.donor.Donor;
import bloodmatch.domain.shared.entity.DomainObject;
import bloodmatch.domain.shared.valueObjects.DomainID;

import java.time.LocalDate;

public class Donation extends DomainObject {

  public enum DonationStatus {
    PENDING,
    COMPLETED,
    CANCELLED
  }

  private Donor donor;
  private DonationRequest request;
  private LocalDate donationDate;
  private BloodCenter bloodCenter;
  private DonationStatus status;

  private Donation(
      DomainID id,
      Donor donor,
      DonationRequest request,
      LocalDate donationDate,
      BloodCenter bloodCenter,
      DonationStatus status) {
    this.id = id;
    this.donor = donor;
    this.request = request;
    this.donationDate = donationDate;
    this.bloodCenter = bloodCenter;
    this.status = status;
  }

  private Donation(
      Donor donor,
      DonationRequest request,
      LocalDate donationDate,
      BloodCenter bloodCenter,
      DonationStatus status) {
    this(DomainID.generate(), donor, request, donationDate, bloodCenter, status);
  }

  // --- Factory methods ---

  static Donation scheduleFromRequest(
      Donor donor,
      DonationRequest request,
      LocalDate expectedDate,
      LocalDate currentDate) {

    if (donor == null)
      throw new IllegalArgumentException("Donor cannot be null");
    if (request == null)
      throw new IllegalArgumentException("Request cannot be null");
    if (expectedDate == null)
      throw new IllegalArgumentException("Expected date cannot be null");
    if (currentDate == null)
      throw new IllegalArgumentException("Current date cannot be null");
    if (!request.isActive())
      throw new IllegalStateException("Request is not active");
    if (!request.getAcceptedDonors().contains(donor))
      throw new IllegalStateException("Donor did not accept the request");
    if (!donor.getBloodType().canDonateTo(request.getBloodTypeNeeded()))
      throw new IllegalStateException("Incompatible blood type");
    if (expectedDate.isBefore(currentDate))
      throw new IllegalArgumentException("Expected date cannot be in the past");

    return new Donation(
        donor,
        request,
        expectedDate,
        request.getBloodCenter(),
        DonationStatus.PENDING);
  }

  static Donation registerExternalDonation(
      Donor donor,
      LocalDate donationDate,
      BloodCenter bloodCenter,
      LocalDate currentDate) {

    if (donor == null)
      throw new IllegalArgumentException("Donor cannot be null");
    if (donationDate == null)
      throw new IllegalArgumentException("Donation date cannot be null");
    if (currentDate == null)
      throw new IllegalArgumentException("Current date cannot be null");
    if (donationDate.isAfter(currentDate))
      throw new IllegalArgumentException("Donation date cannot be in the future");
    if (bloodCenter == null)
      throw new IllegalArgumentException("Blood center cannot be null");

    return new Donation(donor, null, donationDate, bloodCenter, DonationStatus.COMPLETED);
  }

  public static Donation reconstitute(
      DomainID id,
      Donor donor,
      DonationRequest request,
      LocalDate donationDate,
      BloodCenter bloodCenter,
      DonationStatus status) {

    if (id == null)
      throw new IllegalArgumentException("Donation id cannot be null");
    if (donor == null)
      throw new IllegalArgumentException("Donor cannot be null");
    if (donationDate == null)
      throw new IllegalArgumentException("Donation date cannot be null");
    if (bloodCenter == null)
      throw new IllegalArgumentException("Blood center cannot be null");
    if (status == null)
      throw new IllegalArgumentException("Status cannot be null");

    return new Donation(id, donor, request, donationDate, bloodCenter, status);
  }

  // --- Comportamento ---

  void complete(LocalDate completionDate, LocalDate currentDate) {
    if (completionDate == null)
      throw new IllegalArgumentException("Completion date cannot be null");
    if (currentDate == null)
      throw new IllegalArgumentException("Current date cannot be null");
    if (status != DonationStatus.PENDING)
      throw new IllegalStateException("Only pending donations can be completed");
    if (completionDate.isAfter(currentDate))
      throw new IllegalArgumentException("Completion date cannot be in the future");

    this.donationDate = completionDate;
    this.status = DonationStatus.COMPLETED;
  }

  void cancel() {
    if (status != DonationStatus.PENDING)
      throw new IllegalStateException("Only pending donations can be cancelled");
    this.status = DonationStatus.CANCELLED;
  }

  // --- Queries ---

  public boolean isFromRequest() {
    return request != null;
  }

  public boolean isPending() {
    return status == DonationStatus.PENDING;
  }

  public boolean isCompleted() {
    return status == DonationStatus.COMPLETED;
  }

  public Donor getDonor() { return donor; }
  public LocalDate getDonationDate() { return donationDate; }
  public DonationRequest getRequest() { return request; }
  public BloodCenter getBloodCenter() { return bloodCenter; }
  public DonationStatus getStatus() { return status; }
}