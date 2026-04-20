package bloodmatch.interfaces.rest.party.registerorganization;

public record RegisterOrganizationDto(
    String name,
    String cnpj,
    String email,
    String password,
    String passwordConfirmation) {
}
