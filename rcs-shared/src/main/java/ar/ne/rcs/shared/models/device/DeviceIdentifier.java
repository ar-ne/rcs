package ar.ne.rcs.shared.models.device;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class DeviceIdentifier {
    public String imei;
    public String serialNo;
    public String androidId;
}
