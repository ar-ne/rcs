package ar.ne.rcs.server.repo;

import ar.ne.rcs.server.repo.mongo.MongoJobRepo;
import ar.ne.rcs.shared.models.rc.Job;
import ar.ne.rcs.shared.models.rc.JobLifecycle;
import ar.ne.rcs.shared.models.rc.JobMetadata;
import ar.ne.rcs.shared.models.rc.ResultPartial;
import ar.ne.rcs.shared.models.stores.JobStore;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;
import java.util.UUID;
import java.util.stream.Stream;

@Repository
public class JobRepo {
    public final MongoJobRepo repo;
    private final MongoTemplate mongoTemplate;

    public JobRepo(MongoJobRepo repo, MongoTemplate mongoTemplate) {
        this.repo = repo;
        this.mongoTemplate = mongoTemplate;
    }

    public JobStore create(String command, String identifier, Date schedule) {
        String id = UUID.randomUUID().toString();
        JobStore store = JobStore.builder()
                .id(id)
                .job(
                        Job.builder()
                                .id(id)
                                .command(command)
                                .schedule(schedule)
                                .build()
                )
                .metadata(
                        JobMetadata.builder()
                                .id(id)
                                .deviceIdentifier(identifier)
                                .build()
                )
                .build();
        repo.insert(store);
        return store;
    }

    public List<JobStore> list() {
        return repo.findAll();
    }

    public JobStore update(ResultPartial rp) {
        JobStore store = find(rp.getId());

        store.getResultPartials().add(rp);
        if (store.getMetadata().getStatus() != JobLifecycle.FINISHED)
            store.getMetadata().setStatus(rp.getCurrentStatus());

        //update metadata$startTime
        if (store.getResultPartials().size() == 1)
            store.getMetadata().setStartTime(new Date());

        //update metadata when finished
        if (rp.getCurrentStatus().equals(JobLifecycle.FINISHED)) {
            store.getMetadata().setEndTime(new Date());
            store.getMetadata().setExitCode(Integer.parseInt(rp.getContent()));
        }

        return repo.save(store);
    }

    public JobStore find(String id) {
        return repo.findById(id).orElseThrow(() -> new RuntimeException("Unable to find target JobStore: " + id));
    }

    public Stream<JobStore> findAllByIdentifier(String identifier) {
        return repo.findAll().stream().filter(jobStore -> jobStore.getMetadata().getDeviceIdentifier().equals(identifier));
    }
}
