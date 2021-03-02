package ar.ne.rcs.server.controller.rest;

import ar.ne.rcs.server.serivce.JobService;
import ar.ne.rcs.shared.models.rc.JobMetadata;
import ar.ne.rcs.shared.models.rc.ResultPartial;
import ar.ne.rcs.shared.models.stores.JobStore;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.web.bind.annotation.*;

import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

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
    public String create(@RequestBody CreateDeviceRequest request) {
        return service.create(request.command, request.identifier, request.schedule);
    }

    @GetMapping("list")
    public List<JobMetadata> list() {
        return service.list().stream().map(JobStore::getMetadata).collect(Collectors.toList());
    }

    @GetMapping("result")
    public List<ResultPartial> result(String id) {
        return service.getStore(id).getResultPartials();
    }

    @GetMapping("info/{id}")
    public JobStore info(@PathVariable String id) {
        return service.getStore(id);
    }

    private static class CreateDeviceRequest {
        public String command;
        public String identifier;
        public Date schedule;
    }
}
