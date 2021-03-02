package ar.ne.rcs.server.repo.mongo;

import ar.ne.rcs.shared.models.stores.VersionStore;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MongoVersionRepo extends MongoRepository<VersionStore, String> {
}
