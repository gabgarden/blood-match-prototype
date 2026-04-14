package bloodmatch.interfaces.rest.role;

import bloodmatch.application.usecase.role.RegisterDonorUseCase;
import bloodmatch.application.usecase.role.RegisterRequesterUseCase;
import bloodmatch.application.usecase.role.UpdateDonorProfileUseCase;
import bloodmatch.domain.roles.person.donor.Donor;
import bloodmatch.domain.roles.requester.Requester;
import bloodmatch.domain.shared.valueObjects.BloodType;
import bloodmatch.domain.shared.valueObjects.DomainID;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping
public class RoleController {

  private final RegisterRequesterUseCase registerRequesterUseCase;
  private final RegisterDonorUseCase registerDonorUseCase;
  private final UpdateDonorProfileUseCase updateDonorProfileUseCase;

  public RoleController(
      RegisterRequesterUseCase registerRequesterUseCase,
      RegisterDonorUseCase registerDonorUseCase,
      UpdateDonorProfileUseCase updateDonorProfileUseCase) {
    this.registerRequesterUseCase = registerRequesterUseCase;
    this.registerDonorUseCase = registerDonorUseCase;
    this.updateDonorProfileUseCase = updateDonorProfileUseCase;
  }

  @PostMapping("/requesters")
  public ResponseEntity<Map<String, String>> registerRequester(@RequestBody RegisterRequesterDto payload) {

    try {
      if (payload == null)
        throw new IllegalArgumentException("Request body cannot be null");
      if (isBlank(payload.partyId()))
        throw new IllegalArgumentException("partyId cannot be blank");

      DomainID partyId = parseDomainId(payload.partyId(), "partyId");
      Requester requester = registerRequesterUseCase.execute(partyId);

      return ResponseEntity.status(HttpStatus.CREATED)
          .body(Map.of("id", requester.getId().getValue().toString()));

    } catch (IllegalArgumentException | IllegalStateException e) {
      return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
    }
  }

  @PostMapping("/donors")
  public ResponseEntity<Map<String, String>> registerDonor(@RequestBody RegisterDonorDto payload) {

    try {
      if (payload == null)
        throw new IllegalArgumentException("Request body cannot be null");
      if (isBlank(payload.personId()))
        throw new IllegalArgumentException("personId cannot be blank");
      if (isBlank(payload.bloodType()))
        throw new IllegalArgumentException("bloodType cannot be blank");
      if (payload.weight() == null)
        throw new IllegalArgumentException("weight cannot be null");

      DomainID personId = parseDomainId(payload.personId(), "personId");
      Donor donor = registerDonorUseCase.execute(
          personId,
          BloodType.of(payload.bloodType()),
          payload.weight());

      return ResponseEntity.status(HttpStatus.CREATED)
          .body(Map.of("id", donor.getId().getValue().toString()));

    } catch (IllegalArgumentException | IllegalStateException e) {
      return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
    }
  }

  @PatchMapping("/donors/{personId}/profile")
  public ResponseEntity<Map<String, String>> updateDonorProfile(
      @PathVariable String personId,
      @RequestBody UpdateDonorProfileDto payload) {

    try {
      if (isBlank(personId))
        throw new IllegalArgumentException("personId cannot be blank");
      if (payload == null)
        throw new IllegalArgumentException("Request body cannot be null");
      if (isBlank(payload.bloodType()))
        throw new IllegalArgumentException("bloodType cannot be blank");
      if (payload.weight() == null)
        throw new IllegalArgumentException("weight cannot be null");

      DomainID id = parseDomainId(personId, "personId");
      Donor donor = updateDonorProfileUseCase.execute(
          id,
          BloodType.of(payload.bloodType()),
          payload.weight());

      return ResponseEntity.ok(Map.of("id", donor.getId().getValue().toString()));

    } catch (IllegalArgumentException | IllegalStateException e) {
      return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
    }
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
