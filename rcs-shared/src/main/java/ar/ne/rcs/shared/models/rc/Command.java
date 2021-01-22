package ar.ne.rcs.shared.models.rc;

import lombok.Builder;
import lombok.Data;

/**
 * 在client上执行的命令
 */
@Data
@Builder
public class Command {
    /**
     * 自增id
     */
    int id;
    String command;
    @Builder.Default
    Status status = Status.ADDED;
}
