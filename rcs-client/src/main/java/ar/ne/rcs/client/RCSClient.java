package ar.ne.rcs.client;

import ar.ne.rcs.client.communication.HttpConfig;
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

    private RCSClient(CommunicationConfigModel communicationConfigModel) {
        if (RCS_CLIENT != null) throw new RuntimeException("Duplicate initialization of RCSClient!");
        this.httpConfig = HttpConfig.initial(communicationConfigModel);
    }

    public static RCSClient initialization(CommunicationConfigModel communicationConfigModel) {
        RCS_CLIENT = new RCSClient(communicationConfigModel);
        return RCS_CLIENT;
    }
}
