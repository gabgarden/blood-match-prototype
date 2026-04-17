package bloodmatch.application.usecase.donation.acceptandcreatependingfromrequest;

import bloodmatch.application.usecase.donation.creatependingfromrequest.CreatePendingDonationFromRequestUseCase;
import bloodmatch.application.usecase.donationrequest.AcceptDonorInRequestUseCase;
import bloodmatch.domain.donation.Donation;
import bloodmatch.domain.shared.valueObjects.DomainID;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;

@Service
public class AcceptDonorAndCreatePendingDonationFromRequestUseCase {

  private final AcceptDonorInRequestUseCase acceptDonorInRequestUseCase;
  private final CreatePendingDonationFromRequestUseCase createPendingDonationFromRequestUseCase;

  public AcceptDonorAndCreatePendingDonationFromRequestUseCase(
      AcceptDonorInRequestUseCase acceptDonorInRequestUseCase,
      CreatePendingDonationFromRequestUseCase createPendingDonationFromRequestUseCase) {
    this.acceptDonorInRequestUseCase = acceptDonorInRequestUseCase;
    this.createPendingDonationFromRequestUseCase = createPendingDonationFromRequestUseCase;
  }

  @Transactional
  public Donation execute(
      DomainID requestId,
      DomainID donorId,
      LocalDate expectedDate) {

    return execute(requestId, donorId, expectedDate, LocalDate.now());
  }

  @Transactional
  public Donation execute(
      DomainID requestId,
      DomainID donorId,
      LocalDate expectedDate,
      LocalDate currentDate) {

    if (requestId == null)
      throw new IllegalArgumentException("Request id cannot be null");
    if (donorId == null)
      throw new IllegalArgumentException("Donor id cannot be null");
    if (expectedDate == null)
      throw new IllegalArgumentException("Expected date cannot be null");
    if (currentDate == null)
      throw new IllegalArgumentException("Current date cannot be null");

    acceptDonorInRequestUseCase.execute(requestId, donorId, currentDate);

    return createPendingDonationFromRequestUseCase.execute(
        donorId,
        requestId,
        expectedDate,
        currentDate);
  }
}
