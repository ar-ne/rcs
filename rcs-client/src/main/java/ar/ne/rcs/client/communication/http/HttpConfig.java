package ar.ne.rcs.client.communication.http;

import ar.ne.rcs.shared.models.configs.CommunicationConfigModel;

public class HttpConfig {
    private static volatile HttpConfig Instance = null;
    public final String baseUrl;
    public final CommunicationConfigModel communicationConfigModel;

    private HttpConfig(CommunicationConfigModel communicationConfigModel) {
        baseUrl = String.format("%s/%s", communicationConfigModel.getAPIBaseURL(), communicationConfigModel.getDeviceID());
        this.communicationConfigModel = communicationConfigModel;
    }

    public static HttpConfig initial(CommunicationConfigModel communicationConfigModel) {
        Instance = new HttpConfig(communicationConfigModel);
        return Instance;
    }

    public static HttpConfig getInstance() throws RuntimeException {
        if (Instance == null) throw new ConfigNotReadyException();
        return Instance;
    }

    public static class ConfigNotReadyException extends RuntimeException {
        public ConfigNotReadyException() {
            super("Config not ready to use, you had to call initial first!");
        }
    }
}
