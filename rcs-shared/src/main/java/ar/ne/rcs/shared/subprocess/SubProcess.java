package ar.ne.rcs.shared.subprocess;

import org.zeroturnaround.exec.ProcessExecutor;
import org.zeroturnaround.exec.ProcessResult;

import java.io.IOException;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.concurrent.TimeoutException;
import java.util.logging.Logger;

public class SubProcess extends Thread {
    private final String[] command;
    public ProcessResult pr;
    public LinkedList<String> output = new LinkedList<>();
    Logger logger = Logger.getLogger(this.getClass().getName());

    public SubProcess(String... command) {
        this.command = command;
        logger.info("Starting new subprocess with command: " + Arrays.toString(command));
        setName("SubProc");
        start();
    }

    @Override
    public void run() {
        try {
            pr = new ProcessExecutor().command(command).destroyOnExit().execute();
        } catch (IOException | InterruptedException | TimeoutException e) {
            e.printStackTrace();
        }
    }

    public int getExitCode() {
        return pr.getExitValue();
    }

    public String getOutput() {
        return pr.outputString();
    }
}

