package ar.ne.rcs.server.jms;

import ar.ne.rcs.server.serivce.WSService;
import org.springframework.stereotype.Component;

@Component
abstract class BaseReceiver {
    protected final WSService wsService;

    protected BaseReceiver(WSService wsService) {
        this.wsService = wsService;
    }
}
