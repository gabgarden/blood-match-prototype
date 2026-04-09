package bloodmatch.domain.matching;

import bloodmatch.domain.donationRequest.DonationRequest;
import bloodmatch.domain.roles.person.donor.Donor;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class DonorMatchingService {

    public List<Donor> findEligibleDonors(
            DonationRequest request,
            List<Donor> donors,
            LocalDate currentDate) {

        if (request == null)
            throw new IllegalArgumentException("Request cannot be null");

        if (donors == null)
            throw new IllegalArgumentException("Donors list cannot be null");

        if (currentDate == null)
            throw new IllegalArgumentException("Current date cannot be null");

        List<Donor> eligibleDonors = new ArrayList<>();

        for (Donor donor : donors) {

            if (donor == null)
                continue;

            if (!request.canBeFulfilledBy(donor.getBloodType(), currentDate))
                continue;

            if (!donor.isEligibleToDonate(currentDate))
                continue;

            eligibleDonors.add(donor);
        }

        return eligibleDonors;
    }

}