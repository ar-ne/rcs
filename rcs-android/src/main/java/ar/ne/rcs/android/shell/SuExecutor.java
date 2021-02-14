package ar.ne.rcs.android.shell;

import ar.ne.rcs.client.utilities.shell.Executor;
import ar.ne.rcs.client.utilities.shell.Result;
import ar.ne.rcs.shared.models.rc.Job;
import ar.ne.rcs.shared.models.rc.JobStatus;
import ar.ne.rcs.shared.models.rc.ResultPartial;
import com.topjohnwu.superuser.BusyBoxInstaller;
import com.topjohnwu.superuser.CallbackList;
import com.topjohnwu.superuser.Shell;

import java.util.LinkedList;
import java.util.List;
import java.util.function.Function;

/**
 * a rooted shell executor using {@link com.topjohnwu.superuser.Shell}
 */
public class SuExecutor extends Executor {

    static {
        Shell.enableVerboseLogging = true;
        Shell.setDefaultBuilder(Shell.Builder.create()
                .setFlags(Shell.FLAG_REDIRECT_STDERR)
                .setTimeout(10)
                .setInitializers(BusyBoxInstaller.class)
        );
    }

    public SuExecutor() {
        super(true);
    }

    @Override
    public Result exec(String command) {
        LinkedList<String> out = new LinkedList<>();
        Shell.Result r = Shell.su(command)
                .to(out)
                .exec();
        return new SuResult(r);
    }

    @Override
    public void exec(Job job, Function<ResultPartial, Void> onOutput, Function<ResultPartial, Void> onFinished) {
        List<String> callbackList = new CallbackList<String>() {
            @Override
            public void onAddElement(String s) {
                onOutput.apply(ResultPartial.builder()
                        .id(job.getId())
                        .currentStatus(JobStatus.RUNNING)
                        .content(s)
                        .build());
            }
        };
        Shell.su(job.getCommand())
                .to(callbackList)
                .submit(out -> onFinished.apply(ResultPartial.builder()
                        .id(job.getId())
                        .currentStatus(JobStatus.FINISHED)
                        .content(String.valueOf(out.getCode()))
                        .build()));
    }
}
