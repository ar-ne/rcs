package ar.ne.rcs.client.utilities.shell;

import ar.ne.rcs.client.RCSClient;
import ar.ne.rcs.shared.models.rc.Job;
import ar.ne.rcs.shared.models.rc.ResultPartial;

import java.util.function.Function;

/**
 * Shell job executor.
 */
public abstract class Executor {
    protected boolean privileged;

    /**
     * <p>Don't new it yourself, you can find one from {@link RCSClient#getExecutor()}.</p>
     *
     * @param privileged are we root?
     */
    public Executor(boolean privileged) {
        this.privileged = privileged;
    }

    /**
     * synchronized run a job
     *
     * @param command the job to run, with args
     * @return result of that job
     */
    abstract public Result exec(String command);

    /**
     * run {@link Job} in async
     *
     * @param job        the {@link Job} to run
     * @param onOutput   a {@link Function} called when new output
     * @param onFinished a {@link Function} called when job finished
     */
    abstract public void exec(Job job, Function<ResultPartial, Void> onOutput, Function<ResultPartial, Void> onFinished);
}
