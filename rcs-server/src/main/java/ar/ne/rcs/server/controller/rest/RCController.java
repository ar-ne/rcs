package ar.ne.rcs.server.controller.rest;

import ar.ne.rcs.server.serivce.JobService;
import ar.ne.rcs.shared.models.common.RESTResult;
import ar.ne.rcs.shared.models.device.DeviceIdentifier;
import ar.ne.rcs.shared.models.store.JobStore;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequestMapping("job")
@RestController
public class RCController {
    final JobService service;
    final JmsTemplate jmsTemplate;

    public RCController(JobService service, JmsTemplate jmsTemplate) {
        this.service = service;
        this.jmsTemplate = jmsTemplate;
    }


    @PostMapping("create")
    public RESTResult create(@RequestBody Request request) {
        service.create(request.command, request.identifier);
        return new RESTResult(200);
    }

    @GetMapping("list")
    public List<JobStore> list() {
        return service.list();
    }

    private static class Request {
        public String command;
        public DeviceIdentifier identifier;
    }
}
