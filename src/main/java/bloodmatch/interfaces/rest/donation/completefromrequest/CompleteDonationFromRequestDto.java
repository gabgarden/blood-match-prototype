package bloodmatch.interfaces.rest.donation.completefromrequest;

import java.time.LocalDate;

public record CompleteDonationFromRequestDto(
    String donationId,
    LocalDate completionDate) {
}
