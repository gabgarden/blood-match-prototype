package bloodmatch.domain.roles.person.donor;

import bloodmatch.domain.party.Person;
import bloodmatch.domain.shared.valueObjects.BloodType;

import java.time.LocalDate;

public class MaleDonor extends Donor {

  public MaleDonor(
      Person person,
      BloodType bloodType,
      double weight) {

    super(person, bloodType, weight);
  }

  @Override
  public boolean isEligibleToDonate(LocalDate currentDate) {

    if (!hasValidAge(currentDate))
      return false;

    if (lastDonationDate == null)
      return true;

    return !lastDonationDate
        .plusMonths(3)
        .isAfter(currentDate);
  }

}
