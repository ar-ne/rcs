package ar.ne.rcs.shared.models.store;

import ar.ne.rcs.shared.models.device.DeviceIdentifier;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class DeviceRegistration {
    DeviceIdentifier identifier;
    /**
     * last register time
     */
    @Builder.Default
    Date date = new Date();
    /**
     * websocket session id
     */
    String id;
}
