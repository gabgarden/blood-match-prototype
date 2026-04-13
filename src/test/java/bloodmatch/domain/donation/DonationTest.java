package bloodmatch.domain.donation;

import bloodmatch.domain.donationRequest.DonationRequest;
import bloodmatch.domain.party.Person;
import bloodmatch.domain.party.Organization;
import bloodmatch.domain.roles.organization.bloodcenter.BloodCenter;
import bloodmatch.domain.roles.person.donor.Donor;
import bloodmatch.domain.roles.requester.Requester;
import bloodmatch.domain.shared.valueObjects.BloodType;
import bloodmatch.domain.shared.valueObjects.CNPJ;
import bloodmatch.domain.shared.valueObjects.CPF;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

class DonationTest {

  @Test
  void shouldCreateExternalDonation() {
    LocalDate today = LocalDate.now();

    Donor donor = createEligibleDonor("O-", today);
    BloodCenter bloodCenter = createBloodCenter("12345678000100");

    Donation donation = Donation.registerExternalDonation(
        donor,
        today,
        bloodCenter);

    assertNotNull(donation);
    assertFalse(donation.isFromRequest());
    assertEquals(donor, donation.getDonor());
    assertEquals(today, donation.getDonationDate());
    assertEquals(bloodCenter, donation.getBloodCenter());
    assertNull(donation.getRequest());
  }

  @Test
  void shouldCreateDonationFromRequest() {
    LocalDate today = LocalDate.now();

    Donor donor = createEligibleDonor("O-", today);
    Requester requester = createRequester();
    BloodCenter bloodCenter = createBloodCenter("12345678000100");

    DonationRequest request = DonationRequest.create(
        requester,
        bloodCenter,
        BloodType.of("A+"),
        today.plusDays(10));

    Donation donation = Donation.registerFromRequest(
        donor,
        request,
        today,
        bloodCenter);

    assertNotNull(donation);
    assertTrue(donation.isFromRequest());
    assertEquals(request, donation.getRequest());
    assertEquals(donor, donation.getDonor());
    assertEquals(today, donation.getDonationDate());
    assertEquals(bloodCenter, donation.getBloodCenter());
  }

  @Test
  void shouldThrowWhenDonationDateIsFutureForExternalDonation() {
    LocalDate tomorrow = LocalDate.now().plusDays(1);

    Donor donor = createEligibleDonor("O-", LocalDate.now());
    BloodCenter bloodCenter = createBloodCenter("12345678000100");

    assertThrows(
        IllegalArgumentException.class,
        () -> Donation.registerExternalDonation(donor, tomorrow, bloodCenter));
  }

  @Test
  void shouldThrowWhenDonationDateIsFutureForDonationFromRequest() {
    LocalDate today = LocalDate.now();
    LocalDate tomorrow = today.plusDays(1);

    Donor donor = createEligibleDonor("O-", today);
    Requester requester = createRequester();
    BloodCenter bloodCenter = createBloodCenter("12345678000100");

    DonationRequest request = DonationRequest.create(
        requester,
        bloodCenter,
        BloodType.of("A+"),
        today.plusDays(10));

    assertThrows(
        IllegalArgumentException.class,
        () -> Donation.registerFromRequest(donor, request, tomorrow, bloodCenter));
  }

  @Test
  void shouldThrowWhenBloodCenterDiffersFromRequestBloodCenter() {
    LocalDate today = LocalDate.now();

    Donor donor = createEligibleDonor("O-", today);
    Requester requester = createRequester();
    BloodCenter requestBloodCenter = createBloodCenter("12345678000100");
    BloodCenter anotherBloodCenter = createBloodCenter("00987654000199");

    DonationRequest request = DonationRequest.create(
        requester,
        requestBloodCenter,
        BloodType.of("A+"),
        today.plusDays(10));

    assertThrows(
        IllegalStateException.class,
        () -> Donation.registerFromRequest(
            donor,
            request,
            today,
            anotherBloodCenter));
  }

  @Test
  void shouldThrowWhenRequestIsInactive() {
    LocalDate today = LocalDate.now();

    Donor donor = createEligibleDonor("O-", today);
    Requester requester = createRequester();
    BloodCenter bloodCenter = createBloodCenter("12345678000100");

    DonationRequest request = DonationRequest.create(
        requester,
        bloodCenter,
        BloodType.of("A+"),
        today.plusDays(10));

    request.close();

    assertThrows(
        IllegalStateException.class,
        () -> Donation.registerFromRequest(
            donor,
            request,
            today,
            bloodCenter));
  }

  private Requester createRequester() {
    Person person = new Person(
        "Requester Person",
        new CPF("12345678901"),
        LocalDate.of(1995, 1, 1));

    return new Requester(person);
  }

  private BloodCenter createBloodCenter(String cnpjValue) {
    Organization organization = new Organization(
        "Blood Center " + cnpjValue,
        new CNPJ(cnpjValue));

    BloodCenter bloodCenter = new BloodCenter(organization);
    return bloodCenter;
  }

  private Donor createEligibleDonor(String bloodType, LocalDate currentDate) {
    Person donorPerson = new Person(
        "Donor Person",
        new CPF("98765432100"),
        currentDate.minusYears(30));

    return new Donor(
        donorPerson,
        BloodType.of(bloodType),
        75.0);
  }
}