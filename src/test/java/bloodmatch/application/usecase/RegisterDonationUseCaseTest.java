package bloodmatch.application.usecase;

import bloodmatch.domain.donation.Donation;
import bloodmatch.domain.donation.DonationFactory;
import bloodmatch.domain.party.Person;
import bloodmatch.domain.party.Organization;
import bloodmatch.domain.roles.organization.bloodcenter.BloodCenter;
import bloodmatch.domain.roles.person.donor.MaleDonor;
import bloodmatch.domain.shared.valueObjects.BloodType;
import bloodmatch.domain.shared.valueObjects.CNPJ;
import bloodmatch.domain.shared.valueObjects.CPF;
import bloodmatch.domain.shared.valueObjects.DomainID;
import bloodmatch.interfaces.BloodCenterRepositoryInterface;
import bloodmatch.interfaces.DonorRepositoryInterface;
import bloodmatch.interfaces.DonationRepositoryInterface;
import bloodmatch.interfaces.DonationRequestRepositoryInterface;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class RegisterDonationUseCaseTest {

  private final DonationFactory donationFactory = mock(DonationFactory.class);
    private final DonorRepositoryInterface donorRepository = mock(DonorRepositoryInterface.class);
    private final BloodCenterRepositoryInterface bloodCenterRepository = mock(BloodCenterRepositoryInterface.class);
  private final DonationRequestRepositoryInterface donationRequestRepository = mock(
      DonationRequestRepositoryInterface.class);
  private final DonationRepositoryInterface donationRepository = mock(DonationRepositoryInterface.class);

  private final RegisterDonationUseCase useCase = new RegisterDonationUseCase(
      donationFactory,
            donorRepository,
            bloodCenterRepository,
      donationRequestRepository,
      donationRepository);

  @Test
  void shouldRegisterExternalDonationAndPersistIt() {
    DomainID donorId = DomainID.generate();
    DomainID bloodCenterId = DomainID.generate();
    LocalDate date = LocalDate.of(2026, 3, 16);

    Person donorParty = new Person(
        "Donor Person",
        new CPF("98765432100"),
        LocalDate.of(1996, 1, 1));
    MaleDonor donor = new MaleDonor(donorParty, BloodType.of("O-"), 75.0);

    Organization bloodCenterParty = new Organization(
        "Main Blood Center",
        new CNPJ("12345678000100"));
    BloodCenter bloodCenter = new BloodCenter(bloodCenterParty);

    Donation donation = mock(Donation.class);

    when(donorRepository.findByPartyId(donorId)).thenReturn(Optional.of(donor));
    when(bloodCenterRepository.findByPartyId(bloodCenterId)).thenReturn(Optional.of(bloodCenter));
    when(donationFactory.createExternalDonation(donor, bloodCenter, date)).thenReturn(donation);

    Donation result = useCase.executeExternal(donorId, bloodCenterId, date);

    assertSame(donation, result);
    verify(donationRepository).save(donation);
  }

  @Test
  void shouldThrowWhenDonationRequestIsNotFound() {
    DomainID donorId = DomainID.generate();
    DomainID requestId = DomainID.generate();
    LocalDate date = LocalDate.of(2026, 3, 16);

    Person donorParty = new Person(
        "Donor Person",
        new CPF("98765432100"),
        LocalDate.of(1996, 1, 1));
    MaleDonor donor = new MaleDonor(donorParty, BloodType.of("O-"), 75.0);

    when(donorRepository.findByPartyId(donorId)).thenReturn(Optional.of(donor));
    when(donationRequestRepository.findById(requestId)).thenReturn(Optional.empty());

    assertThrows(
        IllegalArgumentException.class,
        () -> useCase.executeFromRequest(donorId, requestId, date));
  }
}