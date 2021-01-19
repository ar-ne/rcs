package ar.ne.rcs.client.network;

public class Config {
    private static Config Instance = null;
    public String baseUrl;
    public String imei;

    private Config(String apiBase, String imei) {
        this.baseUrl = String.format("%s/%s", apiBase, imei);
        this.imei = imei;
    }

    public static void initial(String apiBase, String imei) {
        Instance = new Config(apiBase, imei);
    }

    public static Config getInstance() throws Exception {
        if (Instance == null) throw new Exception("You had to call initial(server) first!");
        return Instance;
    }
}
