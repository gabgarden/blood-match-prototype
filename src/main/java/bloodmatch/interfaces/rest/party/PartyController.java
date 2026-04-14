package bloodmatch.interfaces.rest.party;

import bloodmatch.application.usecase.party.RegisterPartyUseCase;
import bloodmatch.application.usecase.party.UpdatePartyNameUseCase;
import bloodmatch.domain.party.Organization;
import bloodmatch.domain.party.Party;
import bloodmatch.domain.party.Person;
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
@RequestMapping("/parties")
public class PartyController {

  private final RegisterPartyUseCase registerPartyUseCase;
  private final UpdatePartyNameUseCase updatePartyNameUseCase;

  public PartyController(
      RegisterPartyUseCase registerPartyUseCase,
      UpdatePartyNameUseCase updatePartyNameUseCase) {
    this.registerPartyUseCase = registerPartyUseCase;
    this.updatePartyNameUseCase = updatePartyNameUseCase;
  }

  @PostMapping("/persons")
  public ResponseEntity<Map<String, String>> registerPerson(@RequestBody CreatePersonPartyDto payload) {

    try {
      if (payload == null)
        throw new IllegalArgumentException("Request body cannot be null");
      if (isBlank(payload.name()))
        throw new IllegalArgumentException("name cannot be blank");
      if (isBlank(payload.cpf()))
        throw new IllegalArgumentException("cpf cannot be blank");
      if (payload.birthDate() == null)
        throw new IllegalArgumentException("birthDate cannot be null");

      Person person = registerPartyUseCase.registerPerson(
          payload.name(),
          payload.cpf(),
          payload.birthDate());

      return ResponseEntity.status(HttpStatus.CREATED)
          .body(Map.of(
              "id", person.getId().getValue().toString(),
              "type", "PERSON"));

    } catch (IllegalArgumentException | IllegalStateException e) {
      return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
    }
  }

  @PostMapping("/organizations")
  public ResponseEntity<Map<String, String>> registerOrganization(@RequestBody CreateOrganizationPartyDto payload) {

    try {
      if (payload == null)
        throw new IllegalArgumentException("Request body cannot be null");
      if (isBlank(payload.name()))
        throw new IllegalArgumentException("name cannot be blank");
      if (isBlank(payload.cnpj()))
        throw new IllegalArgumentException("cnpj cannot be blank");

      Organization organization = registerPartyUseCase.registerOrganization(
          payload.name(),
          payload.cnpj());

      return ResponseEntity.status(HttpStatus.CREATED)
          .body(Map.of(
              "id", organization.getId().getValue().toString(),
              "type", "ORGANIZATION"));

    } catch (IllegalArgumentException | IllegalStateException e) {
      return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
    }
  }

  @PatchMapping("/{partyId}/name")
  public ResponseEntity<Map<String, String>> updatePartyName(
      @PathVariable String partyId,
      @RequestBody UpdatePartyNameDto payload) {

    try {
      if (isBlank(partyId))
        throw new IllegalArgumentException("partyId cannot be blank");
      if (payload == null)
        throw new IllegalArgumentException("Request body cannot be null");
      if (isBlank(payload.newName()))
        throw new IllegalArgumentException("newName cannot be blank");

      DomainID id = parseDomainId(partyId, "partyId");
      Party party = updatePartyNameUseCase.execute(id, payload.newName());

      return ResponseEntity.ok(Map.of(
          "id", party.getId().getValue().toString(),
          "name", party.getName()));

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
