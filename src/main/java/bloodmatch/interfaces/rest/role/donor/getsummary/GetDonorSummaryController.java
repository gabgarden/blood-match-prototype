package bloodmatch.interfaces.rest.role.donor.getsummary;

import bloodmatch.application.usecase.donor.getsummary.GetDonorSummaryUseCase;
import bloodmatch.domain.shared.valueObjects.DomainID;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

import static bloodmatch.interfaces.rest.shared.RequestValidationSupport.isBlank;
import static bloodmatch.interfaces.rest.shared.RequestValidationSupport.parseDomainId;

@RestController
@RequestMapping("/donors")
public class GetDonorSummaryController {

  private final GetDonorSummaryUseCase useCase;

  public GetDonorSummaryController(GetDonorSummaryUseCase useCase) {
    this.useCase = useCase;
  }

  @GetMapping("/{donorId}/summary")
  public ResponseEntity<?> get(@PathVariable String donorId) {
    return execute(donorId);
  }

  private ResponseEntity<?> execute(String donorIdValue) {
    try {
      if (isBlank(donorIdValue))
        throw new IllegalArgumentException("donorId cannot be blank");

      DomainID donorId = parseDomainId(donorIdValue, "donorId");
      GetDonorSummaryUseCase.Output output = useCase.execute(donorId);

      return ResponseEntity.ok(Map.of(
          "donorId", output.donorId(),
          "bloodType", output.bloodType(),
          "lastDonationDate", String.valueOf(output.lastDonationDate()),
          "daysRemaining", output.daysRemaining(),
          "livesImpacted", output.livesImpacted()));

    } catch (IllegalArgumentException | IllegalStateException e) {
      return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
    }
  }
}
