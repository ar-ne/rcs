package ar.ne.rcs.server.apiClient;

import org.jetbrains.annotations.Nullable;

import java.util.Map;

/**
 * connect to old version of api
 */
public class OldApiClient {

    /**
     * @param imei device imei
     * @return #Map contains device info,<code>null</code> if occur error or device not found
     */
    @Nullable
    public Map<String, String> deviceInfo(String imei) {
        return null;
    }
}
