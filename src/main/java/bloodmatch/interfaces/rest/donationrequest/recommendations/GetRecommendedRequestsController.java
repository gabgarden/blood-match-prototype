package bloodmatch.interfaces.rest.donationrequest.recommendations;

import bloodmatch.application.usecase.donationrequest.recommendations.GetRecommendedRequestsUseCase;
import bloodmatch.domain.shared.valueObjects.DomainID;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

import static bloodmatch.interfaces.rest.shared.RequestValidationSupport.isBlank;
import static bloodmatch.interfaces.rest.shared.RequestValidationSupport.parseDomainId;

@RestController
@RequestMapping("/requests")
public class GetRecommendedRequestsController {

  private final GetRecommendedRequestsUseCase useCase;

  public GetRecommendedRequestsController(GetRecommendedRequestsUseCase useCase) {
    this.useCase = useCase;
  }

  @GetMapping("/recommendations")
  public ResponseEntity<?> getByQuery(@RequestParam String donorId) {
    return execute(donorId);
  }



  private ResponseEntity<?> execute(String donorIdValue) {
    try {
      if (isBlank(donorIdValue))
        throw new IllegalArgumentException("donorId cannot be blank");

      DomainID donorId = parseDomainId(donorIdValue, "donorId");
      return ResponseEntity.ok(useCase.execute(donorId));

    } catch (IllegalArgumentException | IllegalStateException e) {
      return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
    }
  }
}
