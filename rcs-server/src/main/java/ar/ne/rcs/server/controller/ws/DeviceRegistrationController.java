package ar.ne.rcs.server.controller.ws;

import ar.ne.rcs.server.serivce.DeviceRegistrationService;
import ar.ne.rcs.shared.consts.MessageDestination;
import ar.ne.rcs.shared.models.device.DeviceIdentifier;
import ar.ne.rcs.shared.models.registration.RegistrationResult;
import org.springframework.context.event.EventListener;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.annotation.SendToUser;
import org.springframework.stereotype.Controller;
import org.springframework.web.socket.messaging.SessionDisconnectEvent;

@Controller
public class DeviceRegistrationController {
    final DeviceRegistrationService service;

    public DeviceRegistrationController(DeviceRegistrationService service) {
        this.service = service;
    }


    @MessageMapping(MessageDestination.Fields.DEVICE_REGISTRATION)
    @SendToUser("/topic/" + MessageDestination.Fields.DEVICE_REGISTRATION)
    public RegistrationResult register(@Payload DeviceIdentifier identifier, @Header("simpSessionId") String sessionId) {
        //TODO: verify device
        service.register(identifier, sessionId);
        return RegistrationResult.ACCEPT;
    }

    @EventListener
    public void handleSessionDisconnect(SessionDisconnectEvent event) {
        service.unregister(event.getSessionId());
    }
}