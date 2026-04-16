package bloodmatch.interfaces.rest.role.updatedonorprofile;

public record UpdateDonorProfileDto(
    String personId,
    String bloodType,
    Double weight) {
}
