package bloodmatch.interfaces;

import bloodmatch.domain.party.Party;
import bloodmatch.domain.shared.valueObjects.DomainID;

import java.util.List;
import java.util.Optional;

public interface PartyRepositoryInterface {

  void save(Party party);

  Optional<Party> findById(DomainID id);

  List<Party> findAllById(List<DomainID> ids);
}