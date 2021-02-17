package ar.ne.rcs.shared.models.rc;

import ar.ne.rcs.shared.models.stores.JobStore;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
public class ResultPartial {
    String content;
    /**
     * generated uuid
     * {@link JobStore#getId()}
     */
    String id;
    @Builder.Default
    JobStatus currentStatus = JobStatus.RUNNING;
    /**
     * time of this {@link ResultPartial}
     */
    @Builder.Default
    Long time = System.nanoTime();
}
