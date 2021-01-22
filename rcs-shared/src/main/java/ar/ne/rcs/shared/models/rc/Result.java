package ar.ne.rcs.shared.models.rc;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class Result {
    /**
     * 序号,start from 0
     */
    long serial;
    /**
     * 对应的command id
     */
    long commandId;
    /**
     * exit code,任务未完成时保持<code>null</code>,\n否则设置为退出代码,该任务返回的最后一个result的exit code必不能为null
     */
    @Builder.Default
    Integer exitCode = null;
    /**
     * 内容
     */
    String contend;
}
