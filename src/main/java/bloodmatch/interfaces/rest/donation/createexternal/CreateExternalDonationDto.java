package bloodmatch.interfaces.rest.donation.createexternal;

import java.time.LocalDate;

public record CreateExternalDonationDto(
    String donorId,
    String bloodCenterId,
    LocalDate donationDate) {
}
