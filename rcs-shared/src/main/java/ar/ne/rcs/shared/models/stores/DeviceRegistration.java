package ar.ne.rcs.shared.models.stores;

import ar.ne.rcs.shared.models.devices.DeviceInfo;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldNameConstants;

import java.util.Date;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@FieldNameConstants
public class DeviceRegistration {
    /**
     * Device Identifier
     */
    String id;
    /**
     * last register time
     */
    @Builder.Default
    Date date = new Date();
    /**
     * websocket session id
     */
    String sessionId;
    DeviceInfo info;
}
