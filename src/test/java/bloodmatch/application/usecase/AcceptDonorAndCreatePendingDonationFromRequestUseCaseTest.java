package bloodmatch.application.usecase;

import bloodmatch.application.usecase.donation.acceptandcreatependingfromrequest.AcceptDonorAndCreatePendingDonationFromRequestUseCase;
import bloodmatch.application.usecase.donation.creatependingfromrequest.CreatePendingDonationFromRequestUseCase;
import bloodmatch.application.usecase.donationrequest.AcceptDonorInRequestUseCase;
import bloodmatch.domain.donation.Donation;
import bloodmatch.domain.shared.valueObjects.DomainID;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertSame;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class AcceptDonorAndCreatePendingDonationFromRequestUseCaseTest {

  private final AcceptDonorInRequestUseCase acceptDonorInRequestUseCase = mock(AcceptDonorInRequestUseCase.class);
  private final CreatePendingDonationFromRequestUseCase createPendingDonationFromRequestUseCase = mock(CreatePendingDonationFromRequestUseCase.class);
  private final AcceptDonorAndCreatePendingDonationFromRequestUseCase useCase = new AcceptDonorAndCreatePendingDonationFromRequestUseCase(
      acceptDonorInRequestUseCase,
      createPendingDonationFromRequestUseCase);

  @Test
  void shouldUseSameCurrentDateForAcceptanceAndPendingCreation() {
    DomainID requestId = DomainID.generate();
    DomainID donorId = DomainID.generate();
    LocalDate expectedDate = LocalDate.of(2026, 4, 25);
    LocalDate currentDate = LocalDate.of(2026, 4, 17);

    Donation donation = mock(Donation.class);
    when(createPendingDonationFromRequestUseCase.execute(donorId, requestId, expectedDate, currentDate))
        .thenReturn(donation);

    Donation result = useCase.execute(requestId, donorId, expectedDate, currentDate);

    assertSame(donation, result);
    verify(acceptDonorInRequestUseCase).execute(requestId, donorId, currentDate);
    verify(createPendingDonationFromRequestUseCase).execute(donorId, requestId, expectedDate, currentDate);
  }
}
