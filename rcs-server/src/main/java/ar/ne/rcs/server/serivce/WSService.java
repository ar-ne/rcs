package ar.ne.rcs.server.serivce;

import ar.ne.rcs.shared.models.stores.DeviceRegistration;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.messaging.MessageHeaders;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.messaging.simp.SimpMessageType;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.messaging.simp.user.SimpSession;
import org.springframework.messaging.simp.user.SimpUser;
import org.springframework.messaging.simp.user.SimpUserRegistry;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.web.socket.messaging.SessionSubscribeEvent;

import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Slf4j
@Service
public class WSService {
    private static final ConcurrentHashMap<String, Function<String, Void>> sessionSubscribeEventHandler = new ConcurrentHashMap<>();
    private final SimpMessagingTemplate simpMessagingTemplate;
    private final SimpUserRegistry simpUserRegistry;
    private final DeviceRegistrationService registrationService;

    public WSService(SimpMessagingTemplate simpMessagingTemplate, SimpUserRegistry simpUserRegistry, DeviceRegistrationService registrationService) {
        this.simpMessagingTemplate = simpMessagingTemplate;
        this.simpUserRegistry = simpUserRegistry;
        this.registrationService = registrationService;
    }

    public void broadcast(String dest, Object payload) {
        simpMessagingTemplate.convertAndSend(dest, payload);
    }

    public void sendToSession(String sessionId, String dest, Object payload) {
        simpMessagingTemplate.convertAndSendToUser(sessionId, "/topic/" + dest, payload, createHeaders(sessionId));
    }

    public void sendToUser(String identifier, String dest, Object payload) {
        String sessionId = registrationService.findByIdentifier(identifier).getSessionId();
        sendToSession(sessionId, dest, payload);
    }

    //https://stackoverflow.com/questions/34929578/spring-websocket-sendtosession-send-message-to-specific-session
    private MessageHeaders createHeaders(String sessionId) {
        SimpMessageHeaderAccessor headerAccessor = SimpMessageHeaderAccessor.create(SimpMessageType.MESSAGE);
        headerAccessor.setSessionId(sessionId);
        headerAccessor.setLeaveMutable(true);
        return headerAccessor.getMessageHeaders();
    }

    /**
     * add a handler for certain message destination
     *
     * @param destination the message destination to react
     * @param handler     the handler {@link Function}, will called with session id
     */
    public void setSessionSubscribeEventHandler(String destination, Function<String, Void> handler) {
        sessionSubscribeEventHandler.put(destination, handler);
    }

    public Stream<SimpUser> getUsers() {
        return simpUserRegistry.getUsers().stream();
    }

    @EventListener
    public void handleSessionSubscribeEvent(SessionSubscribeEvent event) {
        SimpMessageHeaderAccessor headers = SimpMessageHeaderAccessor.wrap(event.getMessage());
        if (sessionSubscribeEventHandler.containsKey(headers.getDestination())) {
            sessionSubscribeEventHandler.get(headers.getDestination()).apply(headers.getSessionId());
        }
    }
    //TODO: 添加常用的ws操作，广播，单播...

    @Scheduled(fixedRate = 60000)
    public void clearDeadSession() {
        Set<String> sessionIds = getUsers()
                .map(u -> u.getSessions().stream().map(SimpSession::getId))
                .collect(Collectors.toSet()).stream()
                .flatMap(d -> d).collect(Collectors.toSet());
        Set<DeviceRegistration> pendingRemove = registrationService.findAll().stream()
                .filter(deviceRegistration -> deviceRegistration.getSessionId() == null || sessionIds.contains(deviceRegistration.getSessionId())).collect(Collectors.toSet());

        if (pendingRemove.size() > 0) {
            log.info("ClearDeadSession: {} dead session will be removed", pendingRemove.size());
            pendingRemove.forEach(registrationService::unregister);
        }
    }
}
