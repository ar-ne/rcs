package ar.ne.rcs.client;

import ar.ne.rcs.client.communication.http.HttpConfig;
import ar.ne.rcs.client.communication.ws.WSClient;
import ar.ne.rcs.client.communication.ws.WSConnection;
import ar.ne.rcs.client.utilities.shell.Executor;
import ar.ne.rcs.shared.models.configs.CommunicationConfigModel;
import lombok.Getter;
import lombok.Setter;

public class RCSClient {
    public static volatile RCSClient RCS_CLIENT;

    @Setter
    @Getter
    private Executor Executor;
    @Setter
    @Getter
    private HttpConfig httpConfig;

    public RCSClient(CommunicationConfigModel communicationConfigModel, WSConnection connection) {
        if (RCS_CLIENT != null) throw new RuntimeException("Duplicate initialization of RCSClient!");
        new Thread(() -> {
            this.httpConfig = HttpConfig.initial(communicationConfigModel);

            WSClient.init(connection, communicationConfigModel.deviceID);
            //TODO: init http client

            RCS_CLIENT = this;
        }).start();
    }
}
