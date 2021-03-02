package ar.ne.rcs.server.controller.ws;

import ar.ne.rcs.server.serivce.JobService;
import ar.ne.rcs.shared.consts.MessageDestination;
import ar.ne.rcs.shared.models.rc.ResultPartial;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Controller;

@Controller
public class RCWebsocketController {

    final JobService service;

    public RCWebsocketController(JobService service) {
        this.service = service;
    }

    @MessageMapping(MessageDestination.Fields.COMMAND_UPDATE_RESULT)
    public void update(@Payload ResultPartial resultPartial) {
        service.update(resultPartial);
    }
}
