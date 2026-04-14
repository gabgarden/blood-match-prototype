package bloodmatch.interfaces.rest.role;

public record RegisterDonorDto(
    String personId,
    String bloodType,
    Double weight) {
}
