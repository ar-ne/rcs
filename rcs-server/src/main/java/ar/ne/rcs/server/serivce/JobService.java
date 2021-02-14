package ar.ne.rcs.server.serivce;

import ar.ne.rcs.server.repo.JobRepo;
import ar.ne.rcs.shared.consts.MessageDestination;
import ar.ne.rcs.shared.models.device.DeviceIdentifier;
import ar.ne.rcs.shared.models.rc.ResultPartial;
import ar.ne.rcs.shared.models.store.JobStore;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class JobService extends BaseService {
    private final JobRepo repo;

    public JobService(JmsTemplate jmsTemplate, JobRepo repo) {
        super(jmsTemplate);
        this.repo = repo;
    }

    public List<JobStore> list() {
        return repo.list();
    }

    /**
     * This method will first create the job in db, then inform others there's new job
     *
     * @param command    command to run
     * @param identifier device to run
     */
    public void create(String command, DeviceIdentifier identifier) {
        JobStore dbStore = repo.create(command, identifier);
        jmsTemplate.convertAndSend(MessageDestination.Fields.COMMAND_CREATE, dbStore);
    }


    public void update(ResultPartial resultPartial) {
        jmsTemplate.convertAndSend(MessageDestination.Fields.COMMAND_UPDATE_RESULT, resultPartial);
    }
}
