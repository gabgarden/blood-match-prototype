package bloodmatch.infra.persistence.repository;

import bloodmatch.domain.donationrequest.DonationRequest;
import bloodmatch.domain.repositories.DonationRequestRepositoryInterface;
import bloodmatch.domain.repositories.DonorRepositoryInterface;
import bloodmatch.domain.repositories.PartyRepositoryInterface;
import bloodmatch.domain.repositories.RequesterRepositoryInterface;
import bloodmatch.domain.shared.valueObjects.DomainID;
import bloodmatch.infra.persistence.repository.mongo.DonationRequestMongoRepository;
import bloodmatch.infra.persistence.schema.DonationRequestSchema;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public class DonationRequestRepositoryImpl implements DonationRequestRepositoryInterface {

  private final DonationRequestMongoRepository mongoRepository;
  private final RequesterRepositoryInterface requesterRepository;
  private final PartyRepositoryInterface partyRepository;
  private final DonorRepositoryInterface donorRepository;

  public DonationRequestRepositoryImpl(
      DonationRequestMongoRepository mongoRepository,
      RequesterRepositoryInterface requesterRepository,
      PartyRepositoryInterface partyRepository,
      DonorRepositoryInterface donorRepository) {
    this.mongoRepository = mongoRepository;
    this.requesterRepository = requesterRepository;
    this.partyRepository = partyRepository;
    this.donorRepository = donorRepository;
  }

  @Override
  public void save(DonationRequest request) {
    add(request);
  }

  @Override
  public Optional<DonationRequest> findById(DomainID id) {
    return find(id);
  }

  @Override
  public List<DonationRequest> findActiveRequests() {
    return mongoRepository.findByActive(true)
        .stream()
        .map(this::toDomain)
        .toList();
  }

  public void add(DonationRequest request) {
    if (request == null)
      throw new IllegalArgumentException("DonationRequest cannot be null");

    DonationRequestSchema schema = new DonationRequestSchema(request);
    mongoRepository.save(schema);
  }

  public void remove(DonationRequest request) {
    if (request == null)
      throw new IllegalArgumentException("DonationRequest cannot be null");

    mongoRepository.deleteById(request.getId().getValue().toString());
  }

  public Optional<DonationRequest> find(DomainID id) {
    if (id == null)
      throw new IllegalArgumentException("Domain id cannot be null");

    return mongoRepository.findById(id.getValue().toString())
        .map(this::toDomain);
  }

  private DonationRequest toDomain(DonationRequestSchema schema) {
    return schema.toDomain(requesterRepository, partyRepository, donorRepository);
  }
}
