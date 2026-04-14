package bloodmatch.interfaces.rest.donationrequest;

import java.time.LocalDate;

public record CreateDonationRequestDto(
    String requesterId,
    String bloodCenterId,
    String bloodTypeNeeded,
    LocalDate dateLimit) {
}
