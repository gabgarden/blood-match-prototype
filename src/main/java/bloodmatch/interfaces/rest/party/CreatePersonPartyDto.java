package bloodmatch.interfaces.rest.party;

import java.time.LocalDate;

public record CreatePersonPartyDto(
    String name,
    String cpf,
    LocalDate birthDate) {
}
