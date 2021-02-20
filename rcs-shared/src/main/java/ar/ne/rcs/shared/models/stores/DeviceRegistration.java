package ar.ne.rcs.shared.models.stores;

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
    String deviceIdentifier;
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
