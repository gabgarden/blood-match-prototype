package bloodmatch.infra.persistence.repository.mongo;

import bloodmatch.infra.persistence.schema.DonorSchema;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface DonorMongoRepository extends MongoRepository<DonorSchema, String> {

  Optional<DonorSchema> findByPersonId(String personId);
}
