package ar.ne.rcs.server.jms;

import ar.ne.rcs.server.serivce.JobService;
import ar.ne.rcs.server.serivce.WSService;
import ar.ne.rcs.shared.models.rc.ResultPartial;
import ar.ne.rcs.shared.models.stores.JobStore;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.stereotype.Component;

import static ar.ne.rcs.shared.consts.MessageDestination.Fields.COMMAND_CREATE;
import static ar.ne.rcs.shared.consts.MessageDestination.Fields.COMMAND_UPDATE_RESULT;

@Component
public class CommandReceiver extends BaseReceiver {
    private final JobService service;

    public CommandReceiver(JobService service, WSService wsService) {
        super(wsService);
        this.service = service;
    }

    @SuppressWarnings("deprecation")
    @JmsListener(destination = COMMAND_UPDATE_RESULT, concurrency = "1")
    public void update(ResultPartial resultPartial) {
        service.save(resultPartial);
    }

    @JmsListener(destination = COMMAND_CREATE)
    public void create(JobStore store) {
        wsService.broadcast(COMMAND_CREATE, store);
        wsService.sendToUser(store.getMetadata().getDeviceIdentifier(), COMMAND_CREATE, store);
    }

}
