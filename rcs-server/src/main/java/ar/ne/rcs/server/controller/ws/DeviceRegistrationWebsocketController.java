package ar.ne.rcs.server.controller.ws;

import ar.ne.rcs.server.apiClient.OldApiClient;
import ar.ne.rcs.server.serivce.DeviceRegistrationService;
import ar.ne.rcs.shared.consts.MessageDestination;
import ar.ne.rcs.shared.enums.registration.RegistrationResult;
import ar.ne.rcs.shared.models.devices.DeviceInfo;
import org.springframework.context.event.EventListener;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.annotation.SendToUser;
import org.springframework.stereotype.Controller;
import org.springframework.web.socket.messaging.SessionDisconnectEvent;

@Controller
public class DeviceRegistrationWebsocketController {
    final DeviceRegistrationService service;
    final OldApiClient oldApiClient;

    public DeviceRegistrationWebsocketController(DeviceRegistrationService service, OldApiClient oldApiClient) {
        this.service = service;
        this.oldApiClient = oldApiClient;
    }


    @MessageMapping(MessageDestination.Fields.DEVICE_REGISTRATION)
    @SendToUser("/topic/" + MessageDestination.Fields.DEVICE_REGISTRATION)
    public RegistrationResult register(@Payload String identifier, @Header("simpSessionId") String sessionId) {
        //TODO: verify device
        DeviceInfo info = oldApiClient.deviceInfo(identifier);
        service.register(identifier, sessionId);
        return RegistrationResult.ACCEPT;
    }

    @EventListener
    public void handleSessionDisconnect(SessionDisconnectEvent event) {
        service.unregister(event.getSessionId());
    }
}