package ar.ne.rcs.client.communication.ws;

import ar.ne.rcs.client.feature.PredefinedFunctions;
import ar.ne.rcs.client.feature.RemoteShell;
import ar.ne.rcs.client.utilities.Cronjob;
import ar.ne.rcs.shared.consts.MessageDestination;
import ar.ne.rcs.shared.enums.registration.RegistrationResult;
import ar.ne.rcs.shared.models.rc.Job;
import ar.ne.rcs.shared.models.rc.JobLifecycle;
import ar.ne.rcs.shared.models.rc.ResultPartial;
import lombok.extern.java.Log;

import static ar.ne.rcs.client.feature.FeatureManager.MANAGER;

@Log
public class WSClient {
    public static volatile WSClient WS_CLIENT;
    private final WSConnection connection;

    private WSClient(WSConnection connection) {
        this.connection = connection;
    }

    public static void init(WSConnection connection, String identifier) {
        connection.connect(
                //on success event
                () -> {
                    String dest = MessageDestination.Fields.DEVICE_REGISTRATION;
                    connection.subscribe(dest, true, RegistrationResult.class, (payload) -> {
                        if (payload.equals(RegistrationResult.ACCEPT))
                            log.info("Device registration success!");
                        else
                            log.warning("Device registration failed!");

                        //set WS_CLIENT instance
                        WS_CLIENT = new WSClient(connection);
                        WS_CLIENT.onConnected();
                        return null;
                    });
                    //send device identifier
                    connection.send(dest, identifier);
                },

                //on error event
                () -> {
                    log.warning("Disconnected, reconnecting...");
                    connection.reconnect();
                }
        );
    }

    private void onConnected() {
        subscribeForJob();
    }

    private void subscribeForJob() {
        connection.subscribe(MessageDestination.Fields.COMMAND_CREATE, true, Job.class, this::onJobReceived);
    }

    public Void sendResult(ResultPartial rp) {
        connection.send(MessageDestination.Fields.COMMAND_UPDATE_RESULT, rp);
        return null;
    }

    public Void onJobReceived(Job job) {
        System.out.println("New job received: " + job.toString());
        sendResult(ResultPartial.builder().id(job.getId()).currentStatus(JobLifecycle.WAITING).build());
        Runnable runnable = null;
        switch (job.getType()) {
            case SHELL_COMMAND:
                runnable = () -> MANAGER.getFeature(RemoteShell.class).getExecutor().exec(job, this::sendResult, this::sendResult);
                break;
            case PREDEFINED_FUNCTION:
                runnable = () -> MANAGER.getFeature(PredefinedFunctions.class).callFunction(job.getCommand(), null);
                break;
        }

        if (runnable != null)
            Cronjob.runOnce(runnable, job.getSchedule());

        return null;
    }

}
