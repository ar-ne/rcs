package ar.ne.rcs.server.serivce;

import ar.ne.rcs.server.repo.DeviceRegistrationRepo;
import ar.ne.rcs.server.repo.mongo.MongoVersionRepo;
import ar.ne.rcs.shared.models.stores.DeviceRegistration;
import ar.ne.rcs.shared.models.stores.VersionStore;
import org.springframework.stereotype.Service;

import static ar.ne.rcs.shared.consts.CONSTANTS.DEFAULT_VERSION_GROUP;

@Service
public class VersionService {
    private final MongoVersionRepo repo;
    private final DeviceRegistrationRepo deviceRegistrationRepo;

    public VersionService(MongoVersionRepo repo, DeviceRegistrationRepo deviceRegistrationRepo) {
        this.repo = repo;
        this.deviceRegistrationRepo = deviceRegistrationRepo;
    }

    public VersionStore findByVersionGroup(String versionGroup) {
        return repo.findById(versionGroup).orElseGet(
                () -> repo.findById(DEFAULT_VERSION_GROUP).orElseThrow()
        );
    }

    public VersionStore findByDevice(String identifier) {
        VersionStore versionStore = repo.findById(DEFAULT_VERSION_GROUP).orElse(null);
        DeviceRegistration deviceRegistration = deviceRegistrationRepo.findByDeviceIdentifier(identifier);
        if (deviceRegistration == null || deviceRegistration.getInfo() == null) return versionStore;
        return repo.findById(deviceRegistration.getInfo().getVersionGroup()).orElse(versionStore);
    }
}
