package bloodmatch.application.usecase.donationrequest;

import bloodmatch.domain.donationrequest.DonationRequest;
import bloodmatch.domain.repositories.DonationRequestRepositoryInterface;
import bloodmatch.domain.shared.valueObjects.DomainID;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class CloseDonationRequestUseCase {

  private final DonationRequestRepositoryInterface donationRequestRepository;

  public CloseDonationRequestUseCase(
      DonationRequestRepositoryInterface donationRequestRepository) {
    if (donationRequestRepository == null)
      throw new IllegalArgumentException("DonationRequestRepository cannot be null");

    this.donationRequestRepository = donationRequestRepository;
  }

  @Transactional
  public void execute(DomainID requestId) {

    if (requestId == null)
      throw new IllegalArgumentException("Request id cannot be null");

    // 1. Busca a DonationRequest do repositório (reconstitui domínio)
    DonationRequest request = donationRequestRepository.findById(requestId)
        .orElseThrow(() -> new IllegalArgumentException("Donation request not found"));

    // 2. Muta o domínio (fecha a request)
    request.close();

    // 3. PERSISTÊNCIA EXPLÍCITA: Salva a mutação no MongoDB
    // Sem este save(), a mudança fica somente em memória
    donationRequestRepository.save(request);
  }
}
