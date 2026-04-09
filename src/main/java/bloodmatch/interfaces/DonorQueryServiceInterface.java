package bloodmatch.interfaces;

import java.util.List;
import bloodmatch.domain.shared.valueObjects.BloodType;
import java.time.LocalDate;

import bloodmatch.domain.roles.person.donor.Donor;

public interface DonorQueryServiceInterface {
  List<Donor> findCandidates(BloodType requestedType, LocalDate currentDate);
}
