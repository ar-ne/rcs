package ar.ne.rcs.server.repo.mongo;

import ar.ne.rcs.shared.models.store.DeviceRegistration;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MongoDeviceRegistrationRepo extends MongoRepository<DeviceRegistration, String> {
}
