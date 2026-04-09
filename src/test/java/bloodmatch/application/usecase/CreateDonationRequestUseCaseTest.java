package bloodmatch.application.usecase;

import bloodmatch.domain.donationRequest.DonationRequest;
import bloodmatch.domain.party.Man;
import bloodmatch.domain.party.Organization;
import bloodmatch.domain.roles.organization.bloodcenter.BloodCenter;
import bloodmatch.domain.roles.requester.Requester;
import bloodmatch.domain.shared.valueObjects.BloodType;
import bloodmatch.domain.shared.valueObjects.CNPJ;
import bloodmatch.domain.shared.valueObjects.CPF;
import bloodmatch.domain.shared.valueObjects.DomainID;
import bloodmatch.interfaces.DonationRequestRepositoryInterface;
import bloodmatch.interfaces.PartyRepositoryInterface;
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
  private final PartyRepositoryInterface partyRepository = mock(PartyRepositoryInterface.class);

  private final CreateDonationRequestUseCase useCase = new CreateDonationRequestUseCase(donationRequestRepository,
      partyRepository);

  @Test
  void shouldCreateAndSaveDonationRequest() {
    LocalDate currentDate = LocalDate.of(2026, 3, 16);
    LocalDate dateLimit = currentDate.plusDays(10);

    Man requesterParty = new Man(
        "Requester Person",
        new CPF("12345678901"),
        LocalDate.of(1995, 1, 1));
    Requester requester = new Requester(requesterParty);
    requesterParty.addRole(requester);

    Organization bloodCenterParty = new Organization(
        "Main Blood Center",
        new CNPJ("12345678000100"));
    BloodCenter bloodCenter = new BloodCenter(bloodCenterParty);
    bloodCenterParty.addRole(bloodCenter);

    DomainID requesterId = DomainID.generate();
    DomainID bloodCenterId = DomainID.generate();

    when(partyRepository.findById(requesterId)).thenReturn(Optional.of(requesterParty));
    when(partyRepository.findById(bloodCenterId)).thenReturn(Optional.of(bloodCenterParty));

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
  void shouldThrowWhenRequesterPartyDoesNotHaveRequesterRole() {
    LocalDate currentDate = LocalDate.of(2026, 3, 16);
    DomainID requesterId = DomainID.generate();
    DomainID bloodCenterId = DomainID.generate();

    Man requesterParty = new Man(
        "Requester Person",
        new CPF("12345678901"),
        LocalDate.of(1995, 1, 1));

    Organization bloodCenterParty = new Organization(
        "Main Blood Center",
        new CNPJ("12345678000100"));
    BloodCenter bloodCenter = new BloodCenter(bloodCenterParty);
    bloodCenterParty.addRole(bloodCenter);

    when(partyRepository.findById(requesterId)).thenReturn(Optional.of(requesterParty));
    when(partyRepository.findById(bloodCenterId)).thenReturn(Optional.of(bloodCenterParty));

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