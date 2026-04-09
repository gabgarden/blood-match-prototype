package bloodmatch.domain.roles.person.donor;

import bloodmatch.domain.party.Woman;
import bloodmatch.domain.shared.valueObjects.BloodType;

import java.time.LocalDate;

public class FemaleDonor extends Donor {

  public FemaleDonor(
      Woman woman,
      BloodType bloodType,
      double weight) {

    super(woman, bloodType, weight);
  }

  @Override
  public boolean isEligibleToDonate(LocalDate currentDate) {

    if (!hasValidAge(currentDate))
      return false;

    if (lastDonationDate == null)
      return true;

    return !lastDonationDate
        .plusMonths(4)
        .isAfter(currentDate);
  }

}
