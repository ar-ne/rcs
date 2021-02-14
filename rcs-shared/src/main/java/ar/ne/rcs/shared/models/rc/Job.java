package ar.ne.rcs.shared.models.rc;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 在client上执行的命令
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Job {
    String command;
    String id;
    @Builder.Default
    JobStatus status = JobStatus.ADDED;
}
