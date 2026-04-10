package bloodmatch.application.usecase;

import bloodmatch.domain.donationRequest.DonationRequest;
import bloodmatch.domain.party.Person;
import bloodmatch.domain.party.Organization;
import bloodmatch.domain.roles.organization.bloodcenter.BloodCenter;
import bloodmatch.domain.roles.requester.Requester;
import bloodmatch.domain.shared.valueObjects.BloodType;
import bloodmatch.domain.shared.valueObjects.CNPJ;
import bloodmatch.domain.shared.valueObjects.CPF;
import bloodmatch.domain.shared.valueObjects.DomainID;
import bloodmatch.interfaces.BloodCenterRepositoryInterface;
import bloodmatch.interfaces.DonationRequestRepositoryInterface;
import bloodmatch.interfaces.RequesterRepositoryInterface;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class CreateDonationRequestUseCaseTest {

  private final DonationRequestRepositoryInterface donationRequestRepository = mock(
      DonationRequestRepositoryInterface.class);
    private final RequesterRepositoryInterface requesterRepository = mock(RequesterRepositoryInterface.class);
    private final BloodCenterRepositoryInterface bloodCenterRepository = mock(BloodCenterRepositoryInterface.class);

  private final CreateDonationRequestUseCase useCase = new CreateDonationRequestUseCase(donationRequestRepository,
            requesterRepository,
            bloodCenterRepository);

  @Test
  void shouldCreateAndSaveDonationRequest() {
    LocalDate currentDate = LocalDate.of(2026, 3, 16);
    LocalDate dateLimit = currentDate.plusDays(10);

    Person requesterParty = new Person(
        "Requester Person",
        new CPF("12345678901"),
        LocalDate.of(1995, 1, 1));
    Requester requester = new Requester(requesterParty);

    Organization bloodCenterParty = new Organization(
        "Main Blood Center",
        new CNPJ("12345678000100"));
    BloodCenter bloodCenter = new BloodCenter(bloodCenterParty);

    DomainID requesterId = DomainID.generate();
    DomainID bloodCenterId = DomainID.generate();

    when(requesterRepository.findByPartyId(requesterId)).thenReturn(Optional.of(requester));
    when(bloodCenterRepository.findByPartyId(bloodCenterId)).thenReturn(Optional.of(bloodCenter));

    DonationRequest request = useCase.execute(
        requesterId,
        bloodCenterId,
        BloodType.of("A+"),
        dateLimit,
        currentDate);

    assertNotNull(request);
    assertEquals(currentDate, request.getDateRequested());
    assertEquals(dateLimit, request.getDateLimit());
    verify(donationRequestRepository).save(any(DonationRequest.class));
  }

  @Test
    void shouldThrowWhenRequesterRoleIsMissing() {
    LocalDate currentDate = LocalDate.of(2026, 3, 16);
    DomainID requesterId = DomainID.generate();
    DomainID bloodCenterId = DomainID.generate();

    Organization bloodCenterParty = new Organization(
        "Main Blood Center",
        new CNPJ("12345678000100"));
    BloodCenter bloodCenter = new BloodCenter(bloodCenterParty);

        when(requesterRepository.findByPartyId(requesterId)).thenReturn(Optional.empty());
        when(bloodCenterRepository.findByPartyId(bloodCenterId)).thenReturn(Optional.of(bloodCenter));

    assertThrows(
        IllegalArgumentException.class,
        () -> useCase.execute(
            requesterId,
            bloodCenterId,
            BloodType.of("A+"),
            currentDate.plusDays(10),
            currentDate));
  }
}