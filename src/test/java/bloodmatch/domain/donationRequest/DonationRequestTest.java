package bloodmatch.domain.donationRequest;

import bloodmatch.domain.party.Person;
import bloodmatch.domain.party.Organization;
import bloodmatch.domain.roles.organization.bloodcenter.BloodCenter;
import bloodmatch.domain.roles.person.donor.MaleDonor;
import bloodmatch.domain.roles.person.donor.Donor;
import bloodmatch.domain.roles.requester.Requester;
import bloodmatch.domain.shared.valueObjects.BloodType;
import bloodmatch.domain.shared.valueObjects.CNPJ;
import bloodmatch.domain.shared.valueObjects.CPF;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class DonationRequestTest {

  // aux methods to create valid objects for testing
  private Requester createRequester() {
    Person person = new Person(
        "Gabriel",
        new CPF("12345678901"),
        LocalDate.of(1999, 6, 22));

    return new Requester(person);
  }

  private BloodCenter createBloodCenter() {
    Organization organization = new Organization(
        "Main Blood Bank",
        new CNPJ("12345678000100"));

    BloodCenter bloodCenter = new BloodCenter(organization);
    return bloodCenter;
  }

  private MaleDonor createEligibleMaleDonor(
      String bloodType,
      LocalDate currentDate) {

    Person donorPerson = new Person(
        "Carlos",
        new CPF("98765432100"),
        currentDate.minusYears(30));

    return new MaleDonor(
        donorPerson,
        BloodType.of(bloodType),
        75.0);
  }

  // tests
  @Test
  void shouldCreateDonationRequest() {
    Requester requester = createRequester();
    BloodCenter bloodCenter = createBloodCenter();

    DonationRequest request = DonationRequest.create(
        requester,
        bloodCenter,
        BloodType.of("A+"),
        LocalDate.now().plusDays(10));

    assertTrue(request.isActive());
    assertEquals("A+", request.getBloodTypeNeeded().getType());
  }

  @Test
  void shouldNotCreateDonationRequestWhenRequesterIsNull() {
    BloodCenter bloodCenter = createBloodCenter();

    assertThrows(
        IllegalArgumentException.class,
        () -> DonationRequest.create(
            null,
            bloodCenter,
            BloodType.of("A+"),
            LocalDate.now().plusDays(10)));
  }

  @Test
  void shouldNotAcceptDonorWhenRequestIsExpired() {
    LocalDate currentDate = LocalDate.now();

    Requester requester = createRequester();
    BloodCenter bloodCenter = createBloodCenter();
    MaleDonor donor = createEligibleMaleDonor("O-", currentDate);

    DonationRequest request = DonationRequest.create(
        requester,
        bloodCenter,
        BloodType.of("A+"),
        currentDate.plusDays(1));

    assertThrows(
        IllegalArgumentException.class,
        () -> request.acceptBy(donor, currentDate.plusDays(2)));
  }

  @Test
  void shouldNotAcceptDonorWhenDonorIsNotEligibleOnDate() {
    LocalDate currentDate = LocalDate.now();

    Requester requester = createRequester();
    BloodCenter bloodCenter = createBloodCenter();
    MaleDonor donor = createEligibleMaleDonor("O-", currentDate);

    donor.registerDonation(currentDate.minusMonths(1));

    DonationRequest request = DonationRequest.create(
        requester,
        bloodCenter,
        BloodType.of("A+"),
        currentDate.plusDays(10));

    assertThrows(
        IllegalStateException.class,
        () -> request.acceptBy(donor, currentDate));
  }

  @Test
  void shouldNotAcceptSameDonorTwice() {
    LocalDate currentDate = LocalDate.now();

    Requester requester = createRequester();
    BloodCenter bloodCenter = createBloodCenter();
    MaleDonor donor = createEligibleMaleDonor("O-", currentDate);

    DonationRequest request = DonationRequest.create(
        requester,
        bloodCenter,
        BloodType.of("A+"),
        currentDate.plusDays(10));

    request.acceptBy(donor, currentDate);

    assertThrows(
        IllegalStateException.class,
        () -> request.acceptBy(donor, currentDate));
  }

  @Test
  void shouldNotAllowExternalMutationOfAcceptedDonorsList() {
    LocalDate currentDate = LocalDate.now();

    Requester requester = createRequester();
    BloodCenter bloodCenter = createBloodCenter();
    MaleDonor donor = createEligibleMaleDonor("O-", currentDate);

    DonationRequest request = DonationRequest.create(
        requester,
        bloodCenter,
        BloodType.of("A+"),
        currentDate.plusDays(10));

    request.acceptBy(donor, currentDate);

    List<Donor> acceptedDonors = request.getAcceptedDonors();

    assertThrows(
        UnsupportedOperationException.class,
        () -> acceptedDonors.clear());
  }

  @Test
  void shouldReturnFalseForCanBeFulfilledByWhenRequestIsInactive() {
    LocalDate currentDate = LocalDate.now();

    Requester requester = createRequester();
    BloodCenter bloodCenter = createBloodCenter();

    DonationRequest request = DonationRequest.create(
        requester,
        bloodCenter,
        BloodType.of("A+"),
        currentDate.plusDays(10));

    request.close();

    assertFalse(request.canBeFulfilledBy(BloodType.of("O-"), currentDate));
  }

}