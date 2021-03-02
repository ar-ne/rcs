package ar.ne.rcs.server.serivce;

import ar.ne.rcs.server.repo.JobRepo;
import ar.ne.rcs.shared.consts.MessageDestination;
import ar.ne.rcs.shared.models.rc.Job;
import ar.ne.rcs.shared.models.rc.JobLifecycle;
import ar.ne.rcs.shared.models.rc.ResultPartial;
import ar.ne.rcs.shared.models.stores.DeviceRegistration;
import ar.ne.rcs.shared.models.stores.JobStore;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.stereotype.Service;

import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.stream.Stream;

@Service
public class JobService extends BaseService {
    private final JobRepo repo;

    public JobService(JmsTemplate jmsTemplate, JobRepo repo, DeviceRegistrationService deviceRegistrationService, WSService wsService) {
        super(jmsTemplate);
        this.repo = repo;

        wsService.setSessionSubscribeEventHandler(MessageDestination.Fields.COMMAND_CREATE, (String sessionId) -> {
            DeviceRegistration registration = deviceRegistrationService.findBySessionId(sessionId);
            getTimeoutJob(registration.getId())
                    .forEach(job -> wsService.sendToSession(sessionId, MessageDestination.Fields.COMMAND_CREATE, job));
            return null;
        });
    }

    public List<JobStore> list() {
        return repo.list();
    }

    /**
     * This method will first create the job in db, then inform others there's new job
     *
     * @param command    command to run
     * @param identifier device to run on
     * @param schedule   when to run
     * @return new created {@link Job}'s id
     */
    public String create(String command, String identifier, Date schedule) {
        JobStore dbStore = repo.create(command, identifier, schedule);
        jmsTemplate.convertAndSend(MessageDestination.Fields.COMMAND_CREATE, dbStore);
        return dbStore.getId();
    }


    public void update(ResultPartial resultPartial) {
        jmsTemplate.convertAndSend(MessageDestination.Fields.COMMAND_UPDATE_RESULT, resultPartial);
    }

    /**
     * @param rp result
     * @deprecated should only be used in {@link ar.ne.rcs.server.jms.CommandReceiver}
     */
    @Deprecated
    public void save(ResultPartial rp) {
        repo.update(rp);
    }

    public JobStore getStore(String id) {
        return repo.find(id);
    }

    /**
     * find {@link Job}s that not in {@link JobLifecycle#RUNNING} state, and exceed the {@link Job#getSchedule()} time for 1 minute
     *
     * @param identifier device identifier
     * @return {@link Job}s that match the condition
     */
    public Stream<Job> getTimeoutJob(String identifier) {
        Calendar now = Calendar.getInstance();
        now.add(Calendar.MINUTE, 1);
        Date d = now.getTime();
        return repo.findAllByIdentifier(identifier)
                .filter(jobStore -> jobStore.getMetadata().getStatus().compareTo(JobLifecycle.RUNNING) < 0)
                .map(JobStore::getJob)
                .filter(job -> job.getSchedule() != null && job.getSchedule().compareTo(d) <= 0);
    }
}
