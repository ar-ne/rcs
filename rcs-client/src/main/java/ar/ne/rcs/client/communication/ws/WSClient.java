package ar.ne.rcs.client.communication.ws;

import ar.ne.rcs.client.feature.RemoteShell;
import ar.ne.rcs.shared.consts.MessageDestination;
import ar.ne.rcs.shared.enums.registration.RegistrationResult;
import ar.ne.rcs.shared.models.rc.ResultPartial;
import ar.ne.rcs.shared.models.stores.JobStore;
import lombok.extern.java.Log;

import static ar.ne.rcs.client.feature.FeatureManager.MANAGER;

@Log
public class WSClient {
    public static volatile WSClient WS_CLIENT;
    private final WSConnection connection;

    private WSClient(WSConnection connection) {
        this.connection = connection;
    }

    public static WSClient init(WSConnection connection) {
        connection.connect(
                () -> {
                    String dest = MessageDestination.Fields.DEVICE_REGISTRATION;
                    connection.subscribe(dest, true, RegistrationResult.class, (payload) -> {
                        if (!payload.equals(RegistrationResult.ACCEPT)) {
                            log.warning("Device registration failed!");
                        }
                        log.info("Device registration success!");
                        return null;
                    });
                    //send device identifier
                    connection.send(dest, "DEVICE_IDENTIFIER");
                },
                () -> {
                    log.warning("Connect error, reconnecting...");
                    connection.reconnect();
                }
        );
        WS_CLIENT = new WSClient(connection);
        WS_CLIENT.subscribeForJob();
        return WS_CLIENT;
    }

    private void subscribeForJob() {
        connection.subscribe(MessageDestination.Fields.COMMAND_CREATE, true, JobStore.class, this::onJobReceived);
    }

    public Void sendResult(ResultPartial rp) {
        connection.send(MessageDestination.Fields.COMMAND_UPDATE_RESULT, rp);
        return null;
    }

    public Void onJobReceived(JobStore jobStore) {
        System.out.println("New job received: " + jobStore.toString());
        MANAGER.getFeature(RemoteShell.class).getExecutor().exec(jobStore.getJob(), this::sendResult, this::sendResult);
        return null;
    }
}
