package bloodmatch.domain.donation;

import bloodmatch.domain.roles.organization.bloodcenter.BloodCenter;
import bloodmatch.domain.donationRequest.DonationRequest;
import bloodmatch.domain.roles.person.donor.Donor;
import bloodmatch.domain.shared.entity.DomainObject;
import bloodmatch.domain.shared.valueObjects.DomainID;

import java.time.LocalDate;

public class Donation extends DomainObject {

  private Donor donor;
  private DonationRequest request;
  private LocalDate donationDate;
  private BloodCenter bloodCenter;

  private Donation(
      Donor donor,
      DonationRequest request,
      LocalDate donationDate,
      BloodCenter bloodCenter) {

    this.id = DomainID.generate();
    this.donor = donor;
    this.request = request;
    this.donationDate = donationDate;
    this.bloodCenter = bloodCenter;
  }

  public static Donation registerFromRequest(
      Donor donor,
      DonationRequest request,
      LocalDate donationDate,
      BloodCenter bloodCenter) {
    return registerFromRequest(
        donor,
        request,
        donationDate,
        bloodCenter,
        LocalDate.now());
  }

  public static Donation registerFromRequest(
      Donor donor,
      DonationRequest request,
      LocalDate donationDate,
      BloodCenter bloodCenter,
      LocalDate currentDate) {
    if (donor == null)
      throw new IllegalArgumentException("Donor cannot be null");
    if (request == null)
      throw new IllegalArgumentException("Request cannot be null");
    if (!request.isActive())
      throw new IllegalStateException("Request is not active");
    if (!donor.getBloodType().canDonateTo(request.getBloodTypeNeeded()))
      throw new IllegalStateException("Incompatible blood type");
    if (donationDate == null)
      throw new IllegalArgumentException("Donation date cannot be null");
    if (currentDate == null)
      throw new IllegalArgumentException("Current date cannot be null");
    if (donationDate.isAfter(currentDate))
      throw new IllegalArgumentException("Donation date cannot be in the future");
    if (bloodCenter == null)
      throw new IllegalArgumentException("Blood center cannot be null");
    if (!request.getBloodCenter().equals(bloodCenter))
      throw new IllegalStateException("Blood center does not match request blood center");
    return new Donation(donor, request, donationDate, bloodCenter);
  }

  public static Donation registerExternalDonation(
      Donor donor,
      LocalDate donationDate,
      BloodCenter bloodCenter) {
    return registerExternalDonation(
        donor,
        donationDate,
        bloodCenter,
        LocalDate.now());
  }

  public static Donation registerExternalDonation(
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
    return new Donation(donor, null, donationDate, bloodCenter);
  }

  public boolean isFromRequest() {
    return request != null;
  }

  public Donor getDonor() {
    return donor;
  }

  public LocalDate getDonationDate() {
    return donationDate;
  }

  public DonationRequest getRequest() {
    return request;
  }

  public BloodCenter getBloodCenter() {
    return bloodCenter;
  }
}