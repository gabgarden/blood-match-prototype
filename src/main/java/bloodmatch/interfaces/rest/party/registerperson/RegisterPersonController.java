package bloodmatch.interfaces.rest.party.registerperson;

import bloodmatch.application.usecase.party.RegisterPartyUseCase;
import bloodmatch.domain.party.Person;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

import static bloodmatch.interfaces.rest.shared.RequestValidationSupport.isBlank;

@RestController
@RequestMapping("/parties")
public class RegisterPersonController {

  private final RegisterPartyUseCase registerPartyUseCase;

  public RegisterPersonController(RegisterPartyUseCase registerPartyUseCase) {
    this.registerPartyUseCase = registerPartyUseCase;
  }

  @PostMapping("/persons")
  public ResponseEntity<Map<String, String>> registerPerson(@RequestBody RegisterPersonDto payload) {
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
}
