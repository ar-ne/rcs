package ar.ne.rcs.server.repo;

import ar.ne.rcs.server.repo.mongo.MongoJobRepo;
import ar.ne.rcs.shared.models.rc.Job;
import ar.ne.rcs.shared.models.rc.JobMetadata;
import ar.ne.rcs.shared.models.rc.JobStatus;
import ar.ne.rcs.shared.models.rc.ResultPartial;
import ar.ne.rcs.shared.models.stores.JobStore;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;
import java.util.UUID;

@Repository
public class JobRepo {
    public final MongoJobRepo repo;

    public JobRepo(MongoJobRepo repo) {
        this.repo = repo;
    }

    public JobStore create(String command, String identifier) {
        String id = UUID.randomUUID().toString();
        JobStore store = JobStore.builder()
                .id(id)
                .job(
                        Job.builder()
                                .id(id)
                                .command(command)
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
        JobStore store = get(rp.getId());

        store.getResultPartials().add(rp);
        store.getJob().setStatus(rp.getCurrentStatus());

        //update metadata$startTime
        if (store.getResultPartials().size() == 1)
            store.getMetadata().setStartTime(new Date());

        //update metadata when finished
        if (rp.getCurrentStatus().equals(JobStatus.FINISHED)) {
            store.getMetadata().setEndTime(new Date());
            store.getMetadata().setExitCode(Integer.parseInt(rp.getContent()));
        }

        return repo.save(store);
    }

    public JobStore get(String id) {
        return repo.findById(id).orElseThrow(() -> new RuntimeException("Unable to find target JobStore: " + id));
    }
}
