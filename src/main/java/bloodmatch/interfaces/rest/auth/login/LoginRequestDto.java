package bloodmatch.interfaces.rest.auth.login;

public record LoginRequestDto(
    String email,
    String password) {
}
