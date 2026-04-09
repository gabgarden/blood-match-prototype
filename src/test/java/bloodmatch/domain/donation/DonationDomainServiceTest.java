package bloodmatch.domain.donation;

import bloodmatch.domain.donationRequest.DonationRequest;
import bloodmatch.domain.party.Man;
import bloodmatch.domain.party.Organization;
import bloodmatch.domain.roles.organization.bloodcenter.BloodCenter;
import bloodmatch.domain.roles.person.donor.MaleDonor;
import bloodmatch.domain.roles.requester.Requester;
import bloodmatch.domain.shared.valueObjects.BloodType;
import bloodmatch.domain.shared.valueObjects.CNPJ;
import bloodmatch.domain.shared.valueObjects.CPF;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

class DonationDomainServiceTest {

  private final DonationDomainService service = new DonationDomainService();

  @Test
  void shouldRegisterExternalDonationAndUpdateDonorLastDonationDate() {
    LocalDate today = LocalDate.now();

    MaleDonor donor = createEligibleDonor("O-", today);
    BloodCenter bloodCenter = createBloodCenter("12345678000100");

    Donation donation = service.registerDonation(donor, bloodCenter, today);

    assertNotNull(donation);
    assertFalse(donation.isFromRequest());
    assertEquals(today, donor.getLastDonationDate());
    assertEquals(today, donation.getDonationDate());
  }

  @Test
  void shouldThrowWhenDonorNotEligibleForExternalDonation() {
    LocalDate today = LocalDate.now();

    MaleDonor donor = createEligibleDonor("O-", today);
    donor.registerDonation(today.minusMonths(1));

    BloodCenter bloodCenter = createBloodCenter("12345678000100");

    assertThrows(
        IllegalStateException.class,
        () -> service.registerDonation(donor, bloodCenter, today));
  }

  @Test
  void shouldRegisterDonationFromRequestAndUpdateDonorLastDonationDate() {
    LocalDate today = LocalDate.now();

    MaleDonor donor = createEligibleDonor("O-", today);
    Requester requester = createRequester();
    BloodCenter bloodCenter = createBloodCenter("12345678000100");

    DonationRequest request = DonationRequest.create(
        requester,
        bloodCenter,
        BloodType.of("A+"),
        today.plusDays(10));

    request.acceptBy(donor, today);

    Donation donation = service.registerDonationFromRequest(donor, request, today);

    assertNotNull(donation);
    assertTrue(donation.isFromRequest());
    assertEquals(request, donation.getRequest());
    assertEquals(today, donor.getLastDonationDate());
  }

  @Test
  void shouldThrowWhenDonorDidNotAcceptRequest() {
    LocalDate today = LocalDate.now();

    MaleDonor donor = createEligibleDonor("O-", today);
    Requester requester = createRequester();
    BloodCenter bloodCenter = createBloodCenter("12345678000100");

    DonationRequest request = DonationRequest.create(
        requester,
        bloodCenter,
        BloodType.of("A+"),
        today.plusDays(10));

    assertThrows(
        IllegalStateException.class,
        () -> service.registerDonationFromRequest(donor, request, today));
  }

  @Test
  void shouldThrowWhenDonorNotEligibleInRequestFlow() {
    LocalDate today = LocalDate.now();

    MaleDonor donor = createEligibleDonor("O-", today);
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
        () -> service.registerDonationFromRequest(donor, request, today));
  }

  private Requester createRequester() {
    Man person = new Man(
        "Requester Person",
        new CPF("12345678901"),
        LocalDate.of(1995, 1, 1));

    Requester requester = new Requester(person);
    person.addRole(requester);

    return requester;
  }

  private BloodCenter createBloodCenter(String cnpjValue) {
    Organization organization = new Organization(
        "Blood Center " + cnpjValue,
        new CNPJ(cnpjValue));

    BloodCenter bloodCenter = new BloodCenter(organization);
    organization.addRole(bloodCenter);

    return bloodCenter;
  }

  private MaleDonor createEligibleDonor(String bloodType, LocalDate currentDate) {
    Man donorPerson = new Man(
        "Donor Person",
        new CPF("98765432100"),
        currentDate.minusYears(30));

    return new MaleDonor(
        donorPerson,
        BloodType.of(bloodType),
        75.0);
  }
}