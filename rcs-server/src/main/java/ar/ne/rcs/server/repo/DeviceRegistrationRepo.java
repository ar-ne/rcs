package ar.ne.rcs.server.repo;

import ar.ne.rcs.server.repo.mongo.MongoDeviceRegistrationRepo;
import ar.ne.rcs.shared.models.stores.DeviceRegistration;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Repository;

import java.util.List;

import static org.springframework.data.mongodb.core.query.Criteria.where;

@Slf4j
@Repository
public class DeviceRegistrationRepo {
    private final MongoTemplate mongo;
    private final MongoDeviceRegistrationRepo repo;

    public DeviceRegistrationRepo(MongoTemplate mongo, MongoDeviceRegistrationRepo repo) {
        this.mongo = mongo;
        this.repo = repo;
    }


    public DeviceRegistration register(String identifier, String id) {
        DeviceRegistration reg = DeviceRegistration.builder()
                .id(identifier)
                .sessionId(id)
                .build();
        repo.save(reg);
        log.debug("Device registered: {}", reg.toString());
        return reg;
    }

    @Nullable
    public DeviceRegistration findByDeviceIdentifier(String identifier) {
        return mongo.findOne(Query.query(where(DeviceRegistration.Fields.id).is(identifier)), DeviceRegistration.class);
    }

    @Nullable
    public DeviceRegistration findBySessionId(String id) {
        return repo.findById(id).orElse(null);
    }

    public List<DeviceRegistration> getAll() {
        return repo.findAll();
    }

    public void unregister(String sessionId) {
        repo.findById(sessionId).ifPresent(this::unregister);
    }

    public void unregister(DeviceRegistration deviceRegistration) {
        log.debug("Device disconnected: registration = {}", deviceRegistration);
        deviceRegistration.setSessionId(null);
        repo.save(deviceRegistration);
    }
}
