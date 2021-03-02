package ar.ne.rcs.server.apiClient;

import ar.ne.rcs.shared.models.devices.DeviceInfo;
import org.springframework.stereotype.Component;

@Component
abstract class AbstractClient {
    /**
     * @param identifier device identifier, in old api. it's imei
     * @return {@link DeviceInfo},<code>null</code> if occur error or device not found
     */
    public abstract DeviceInfo deviceInfo(String identifier);
}
