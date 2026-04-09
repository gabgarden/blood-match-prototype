package bloodmatch.domain.donation;

import bloodmatch.domain.donationRequest.DonationRequest;
import bloodmatch.domain.roles.person.donor.Donor;
import bloodmatch.domain.roles.organization.bloodcenter.BloodCenter;

import java.time.LocalDate;

public class DonationDomainService {

  public Donation registerDonation(
      Donor donor,
      BloodCenter bloodCenter,
      LocalDate date) {

    return registerDonation(donor, bloodCenter, date, date);
  }

  public Donation registerDonation(
      Donor donor,
      BloodCenter bloodCenter,
      LocalDate date,
      LocalDate currentDate) {

    if (donor == null)
      throw new IllegalArgumentException("Donor cannot be null");
    if (bloodCenter == null)
      throw new IllegalArgumentException("Blood center cannot be null");
    if (date == null)
      throw new IllegalArgumentException("Date cannot be null");
    if (currentDate == null)
      throw new IllegalArgumentException("Current date cannot be null");
    if (!donor.isEligibleToDonate(currentDate))
      throw new IllegalStateException("Donor not eligible");

    Donation donation = Donation.registerExternalDonation(
        donor,
        date,
        bloodCenter,
        currentDate);
    donor.registerDonation(date, currentDate);

    return donation;

  }

  public Donation registerDonationFromRequest(
      Donor donor,
      DonationRequest request,
      LocalDate date) {

    return registerDonationFromRequest(donor, request, date, date);
  }

  public Donation registerDonationFromRequest(
      Donor donor,
      DonationRequest request,
      LocalDate date,
      LocalDate currentDate) {

    if (donor == null)
      throw new IllegalArgumentException("Donor cannot be null");
    if (request == null)
      throw new IllegalArgumentException("Request cannot be null");
    if (date == null)
      throw new IllegalArgumentException("Date cannot be null");
    if (currentDate == null)
      throw new IllegalArgumentException("Current date cannot be null");

    if (!request.getAcceptedDonors().contains(donor))
      throw new IllegalStateException(
          "Donor did not accept the request");

    if (!donor.isEligibleToDonate(currentDate))
      throw new IllegalStateException("Donor not eligible");

    Donation donation = Donation.registerFromRequest(
        donor,
        request,
        date,
        request.getBloodCenter(),
        currentDate);

    donor.registerDonation(date, currentDate);

    return donation;
  }
}
