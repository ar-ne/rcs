package ar.ne.rcs.client;

import ar.ne.rcs.client.communication.Config;
import ar.ne.rcs.client.utilities.Cronjob;
import ar.ne.rcs.client.utilities.shell.Executor;
import ar.ne.rcs.shared.models.communication.ConfigModel;
import lombok.Getter;
import lombok.Setter;

import static ar.ne.rcs.shared.consts.ClientConst.DEFAULT_CRON_INTERVAL;

public class RCSClient extends Thread {
    public static final RCSClient RCS_CLIENT;

    static {
        RCS_CLIENT = new RCSClient();
    }

    @Setter
    @Getter
    private Executor Executor;
    @Setter
    @Getter
    private Config config;

    private RCSClient() {
        start();
    }

    public RCSClient(Executor executor, ConfigModel configModel) {
        this.Executor = executor;
        this.config = Config.initial(configModel);
    }

    public static void newInstance(Executor executor, ConfigModel configModel) {
        new RCSClient(executor, configModel);
    }

    //check anything updated with httpAPI
    private void startCronjob() {
        Cronjob cronjob = new Cronjob(() -> {

        }, DEFAULT_CRON_INTERVAL);
    }

    @Override
    public void run() {
        super.run();
    }
}
