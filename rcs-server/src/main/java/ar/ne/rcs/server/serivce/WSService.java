package ar.ne.rcs.server.serivce;

import org.springframework.messaging.MessageHeaders;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.messaging.simp.SimpMessageType;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

@Service
public class WSService {
    private final SimpMessagingTemplate simpMessagingTemplate;
    private final DeviceRegistrationService registrationService;

    public WSService(SimpMessagingTemplate simpMessagingTemplate, DeviceRegistrationService registrationService) {
        this.simpMessagingTemplate = simpMessagingTemplate;
        this.registrationService = registrationService;
    }

    public void broadcast(String dest, Object payload) {
        simpMessagingTemplate.convertAndSend(dest, payload);
    }

    public void sendToSession(String sessionId, String dest, Object payload) {
        simpMessagingTemplate.convertAndSendToUser(sessionId, "/topic/" + dest, payload, createHeaders(sessionId));
    }

    public void sendToUser(String identifier, String dest, Object payload) {
        String sessionId = registrationService.findByIdentifier(identifier).getId();
        sendToSession(sessionId, dest, payload);
    }

    //https://stackoverflow.com/questions/34929578/spring-websocket-sendtosession-send-message-to-specific-session
    private MessageHeaders createHeaders(String sessionId) {
        SimpMessageHeaderAccessor headerAccessor = SimpMessageHeaderAccessor.create(SimpMessageType.MESSAGE);
        headerAccessor.setSessionId(sessionId);
        headerAccessor.setLeaveMutable(true);
        return headerAccessor.getMessageHeaders();
    }

    //TODO: 添加常用的ws操作，广播，单播...
}
