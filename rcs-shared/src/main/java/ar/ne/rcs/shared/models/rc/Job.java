package ar.ne.rcs.shared.models.rc;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

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
    /**
     * <p>if not null, job will execute at scheduled time</p>
     * <p>otherwise, will execute immediately</p>
     */
    @Builder.Default
    Date schedule = null;
    @Builder.Default
    JobType type = JobType.SHELL_COMMAND;
}
