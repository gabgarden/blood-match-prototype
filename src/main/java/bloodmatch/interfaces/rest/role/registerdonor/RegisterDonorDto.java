package bloodmatch.interfaces.rest.role.registerdonor;

public record RegisterDonorDto(
    String personId,
    String bloodType,
    Double weight) {
}
