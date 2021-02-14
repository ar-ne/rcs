package ar.ne.rcs.client.communication.ws;

import ar.ne.rcs.client.RCSClient;
import ar.ne.rcs.shared.consts.MessageDestination;
import ar.ne.rcs.shared.models.device.DeviceIdentifier;
import ar.ne.rcs.shared.models.rc.ResultPartial;
import ar.ne.rcs.shared.models.registration.RegistrationResult;
import ar.ne.rcs.shared.models.store.JobStore;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.java.Log;

@Log
public class WSClient {
    private static final ObjectMapper mapper = new ObjectMapper();
    public static volatile WSClient WS_CLIENT;
    private final WSConnection connection;

    public WSClient(WSConnection connection) {
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
                    connection.send(dest,
                            DeviceIdentifier.builder()
                                    .androidId("AndroidID")
                                    .imei("IMEI")
                                    .serialNo("SERIAL_NO")
                                    .build()
                    );
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
        RCSClient.RCS_CLIENT.getExecutor().exec(jobStore.getJob(), this::sendResult, this::sendResult);
        return null;
    }
}
