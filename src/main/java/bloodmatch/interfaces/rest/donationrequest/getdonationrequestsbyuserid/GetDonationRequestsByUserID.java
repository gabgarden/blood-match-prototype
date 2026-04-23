package bloodmatch.interfaces.rest.donationrequest.getdonationrequestsbyuserid;

import bloodmatch.application.usecase.donationrequest.GetDonationRequestsByUserIdUseCase;
import bloodmatch.domain.shared.valueObjects.DomainID;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

import static bloodmatch.interfaces.rest.shared.RequestValidationSupport.isBlank;
import static bloodmatch.interfaces.rest.shared.RequestValidationSupport.parseDomainId;

@RestController
@RequestMapping("/users")
public class GetDonationRequestsByUserID {

	private final GetDonationRequestsByUserIdUseCase useCase;

	public GetDonationRequestsByUserID(GetDonationRequestsByUserIdUseCase useCase) {
		this.useCase = useCase;
	}

	@GetMapping("/{userId}/donation-requests")
	public ResponseEntity<?> get(@PathVariable String userId) {
		try {
			if (isBlank(userId))
				throw new IllegalArgumentException("userId cannot be blank");

			DomainID userDomainId = parseDomainId(userId, "userId");
			return ResponseEntity.ok(useCase.execute(userDomainId));

		} catch (IllegalArgumentException | IllegalStateException e) {
			return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
		}
	}
}
