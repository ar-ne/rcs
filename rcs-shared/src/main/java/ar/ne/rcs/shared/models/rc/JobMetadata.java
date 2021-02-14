package ar.ne.rcs.shared.models.rc;

import ar.ne.rcs.shared.models.device.DeviceIdentifier;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@Builder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
public class JobMetadata {
    String id;
    Date startTime;
    Date endTime;
    @Builder.Default
    Date addTime = new Date();
    DeviceIdentifier deviceIdentifier;
    @Builder.Default
    Integer exitCode = null;
}
