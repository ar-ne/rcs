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
    @Builder.Default
    String content = "";
    /**
     * generated uuid
     * {@link JobStore#getId()}
     */
    String id;
    @Builder.Default
    JobLifecycle currentStatus = JobLifecycle.RUNNING;
    /**
     * time of this {@link ResultPartial}
     */
    @Builder.Default
    Long time = System.nanoTime();
}
