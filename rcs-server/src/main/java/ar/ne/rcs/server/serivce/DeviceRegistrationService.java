package ar.ne.rcs.server.serivce;

import ar.ne.rcs.server.repo.DeviceRegistrationRepo;
import ar.ne.rcs.shared.consts.MessageDestination;
import ar.ne.rcs.shared.models.stores.DeviceRegistration;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
public class DeviceRegistrationService extends BaseService {
    private final DeviceRegistrationRepo repo;

    public DeviceRegistrationService(JmsTemplate jmsTemplate, DeviceRegistrationRepo repo) {
        super(jmsTemplate);
        this.repo = repo;
    }


    public void register(String identifier, String sessionId) {
        DeviceRegistration reg = repo.register(identifier, sessionId);
        jmsTemplate.convertAndSend(MessageDestination.Fields.DEVICE_REGISTRATION, reg);
    }

    public DeviceRegistration findByIdentifier(String identifier) {
        return repo.findByDeviceIdentifier(identifier);
    }

    public void unregister(String sessionId) {
        repo.unregister(sessionId);
        jmsTemplate.convertAndSend(MessageDestination.Fields.DEVICE_OFFLINE, sessionId);
    }

    public List<DeviceRegistration> findAll() {
        return repo.getAll();
    }
}
