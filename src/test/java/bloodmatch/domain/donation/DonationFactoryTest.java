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

class DonationFactoryTest {

  private final DonationFactory factory = new DonationFactory();

  @Test
  void shouldRegisterExternalDonationAndUpdateDonorLastDonationDate() {
    LocalDate today = LocalDate.now();

    Donor donor = createEligibleDonor("O-", today);
    BloodCenter bloodCenter = createBloodCenter("12345678000100");

    Donation donation = factory.createExternalDonation(donor, bloodCenter, today);

    assertNotNull(donation);
    assertFalse(donation.isFromRequest());
    assertEquals(today, donor.getLastDonationDate());
    assertEquals(today, donation.getDonationDate());
  }

  @Test
  void shouldThrowWhenDonorNotEligibleForExternalDonation() {
    LocalDate today = LocalDate.now();

    Donor donor = createEligibleDonor("O-", today);
    donor.registerDonation(today.minusMonths(1));

    BloodCenter bloodCenter = createBloodCenter("12345678000100");

    assertThrows(
        IllegalStateException.class,
        () -> factory.createExternalDonation(donor, bloodCenter, today));
  }

  @Test
  void shouldRegisterDonationFromRequestAndUpdateDonorLastDonationDate() {
    LocalDate today = LocalDate.now();

    Donor donor = createEligibleDonor("O-", today);
    Requester requester = createRequester();
    BloodCenter bloodCenter = createBloodCenter("12345678000100");

    DonationRequest request = DonationRequest.create(
        requester,
        bloodCenter,
        BloodType.of("A+"),
        today.plusDays(10));

    request.acceptBy(donor, today);

    Donation donation = factory.createDonationFromRequest(donor, request, today);

    assertNotNull(donation);
    assertTrue(donation.isFromRequest());
    assertEquals(request, donation.getRequest());
    assertEquals(today, donor.getLastDonationDate());
  }

  @Test
  void shouldThrowWhenDonorDidNotAcceptRequest() {
    LocalDate today = LocalDate.now();

    Donor donor = createEligibleDonor("O-", today);
    Requester requester = createRequester();
    BloodCenter bloodCenter = createBloodCenter("12345678000100");

    DonationRequest request = DonationRequest.create(
        requester,
        bloodCenter,
        BloodType.of("A+"),
        today.plusDays(10));

    assertThrows(
        IllegalStateException.class,
        () -> factory.createDonationFromRequest(donor, request, today));
  }

  @Test
  void shouldThrowWhenDonorNotEligibleInRequestFlow() {
    LocalDate today = LocalDate.now();

    Donor donor = createEligibleDonor("O-", today);
    Requester requester = createRequester();
    BloodCenter bloodCenter = createBloodCenter("12345678000100");

    DonationRequest request = DonationRequest.create(
        requester,
        bloodCenter,
        BloodType.of("A+"),
        today.plusDays(10));

    request.acceptBy(donor, today);
    donor.registerDonation(today.minusMonths(1));

    assertThrows(
        IllegalStateException.class,
        () -> factory.createDonationFromRequest(donor, request, today));
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