package ar.ne.rcs.client.communication;

import ar.ne.rcs.shared.models.communication.ConfigModel;

public class Config {
    private static volatile Config Instance = null;
    public final String baseUrl;
    public final ConfigModel configModel;

    private Config(ConfigModel configModel) {
        baseUrl = String.format("%s/%s", configModel.getAPIBaseURL(), configModel.getDeviceID());
        this.configModel = configModel;
    }

    public static Config initial(ConfigModel configModel) {
        Instance = new Config(configModel);
        return Instance;
    }

    public static Config getInstance() throws RuntimeException {
        if (Instance == null) throw new ConfigNotReadyException();
        return Instance;
    }

    public static class ConfigNotReadyException extends RuntimeException {
        public ConfigNotReadyException() {
            super("Config not ready to use, you had to call initial first!");
        }
    }
}
