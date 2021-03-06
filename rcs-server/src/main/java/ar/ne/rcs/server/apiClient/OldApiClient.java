package ar.ne.rcs.server.apiClient;

import ar.ne.rcs.shared.models.devices.DeviceInfo;
import org.jetbrains.annotations.Nullable;
import org.springframework.stereotype.Component;

/**
 * connect to old version of api
 */
@Component
public class OldApiClient extends AbstractClient {

    public OldApiClient() {
    }

    /**
     * @param identifier device identifier, in old api. it's imei
     * @return {@link DeviceInfo},<code>null</code> if occur error or device not found
     */
    @Nullable
    @Override
    public DeviceInfo deviceInfo(String identifier) {
        return DeviceInfo.builder().build();
    }
}
