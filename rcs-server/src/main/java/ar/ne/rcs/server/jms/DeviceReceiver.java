package ar.ne.rcs.server.jms;

import ar.ne.rcs.server.serivce.WSService;
import ar.ne.rcs.shared.consts.MessageDestination;
import ar.ne.rcs.shared.models.stores.DeviceRegistration;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.stereotype.Component;

@Component
public class DeviceReceiver extends BaseReceiver {
    protected DeviceReceiver(WSService wsService) {
        super(wsService);
    }

    @JmsListener(destination = MessageDestination.Fields.DEVICE_REGISTRATION)
    public void deviceOnline(DeviceRegistration registration) {
        wsService.broadcast(MessageDestination.Fields.DEVICE_REGISTRATION, registration);
    }

    @JmsListener(destination = MessageDestination.Fields.DEVICE_OFFLINE)
    public void deviceOffline(String sessionId) {
        wsService.broadcast(MessageDestination.Fields.DEVICE_OFFLINE, sessionId);
    }
}
