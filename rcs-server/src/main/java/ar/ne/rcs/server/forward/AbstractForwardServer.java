package ar.ne.rcs.server.forward;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;

@Slf4j
@Component
public abstract class AbstractForwardServer {
    @Value("${forward.server.type}")
    private String type;

    @Value("${forward.server.port}")
    private int port;

    @PostConstruct
    private void start() {
        if (!type.equals(getServerType())) return;
        log.info("Starting forward server， type: {}", getServerType());
        startup(port);
    }

    @PreDestroy
    private void stop() {
        if (!type.equals(getServerType())) return;
        log.info("Stopping forward server， type: {}", getServerType());
        shutdown();
    }

    abstract String getServerType();

    abstract void startup(int port);

    abstract void shutdown();
}
