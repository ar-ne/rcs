package ar.ne.rcs.android.shell;

import ar.ne.rcs.client.utilities.shell.Result;
import com.topjohnwu.superuser.Shell;


public class SuResult extends Result {
    private final Shell.Result result;

    public SuResult(Shell.Result result) {
        this.result = result;
    }

    public String getOutput() {
        StringBuilder builder = new StringBuilder();
        for (String s : result.getOut()) builder.append(s).append("\n");
        return builder.toString();
    }

    @Override
    public int getExitCode() {
        return result.getCode();
    }
}
