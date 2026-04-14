package bloodmatch.interfaces.rest.donationrequest;

import bloodmatch.application.usecase.donationrequest.CreateDonationRequestUseCase;
import bloodmatch.domain.donationrequest.DonationRequest;
import bloodmatch.domain.shared.valueObjects.BloodType;
import bloodmatch.domain.shared.valueObjects.DomainID;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/donation-requests")
public class DonationRequestController {

  private final CreateDonationRequestUseCase useCase;

  public DonationRequestController(CreateDonationRequestUseCase useCase) {
    this.useCase = useCase;
  }

  @PostMapping
  public ResponseEntity<Map<String, String>> create(@RequestBody CreateDonationRequestDto payload) {

    try {
      validatePayload(payload);

      DomainID requesterId = parseDomainId(payload.requesterId(), "requesterId");
      DomainID bloodCenterId = parseDomainId(payload.bloodCenterId(), "bloodCenterId");
      BloodType bloodTypeNeeded = BloodType.of(payload.bloodTypeNeeded());

      DonationRequest request = useCase.execute(
          requesterId,
          bloodCenterId,
          bloodTypeNeeded,
          payload.dateLimit());

      return ResponseEntity
          .status(HttpStatus.CREATED)
          .body(Map.of("id", request.getId().getValue().toString()));

    } catch (IllegalArgumentException e) {
      return ResponseEntity
          .badRequest()
          .body(Map.of("error", e.getMessage()));
    }
  }

  private static void validatePayload(CreateDonationRequestDto payload) {

    if (payload == null)
      throw new IllegalArgumentException("Request body cannot be null");

    if (isBlank(payload.requesterId()))
      throw new IllegalArgumentException("requesterId cannot be blank");

    if (isBlank(payload.bloodCenterId()))
      throw new IllegalArgumentException("bloodCenterId cannot be blank");

    if (isBlank(payload.bloodTypeNeeded()))
      throw new IllegalArgumentException("bloodTypeNeeded cannot be blank");

    if (payload.dateLimit() == null)
      throw new IllegalArgumentException("dateLimit cannot be null");
  }

  private static DomainID parseDomainId(String value, String fieldName) {

    try {
      return new DomainID(UUID.fromString(value));
    } catch (IllegalArgumentException e) {
      throw new IllegalArgumentException(fieldName + " must be a valid UUID");
    }
  }

  private static boolean isBlank(String value) {
    return value == null || value.isBlank();
  }
}
