package bloodmatch.interfaces.rest.role;

public record UpdateDonorProfileDto(
    String bloodType,
    Double weight) {
}
