package ar.ne.rcs.shared.models.device;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class Identifier {
    public String imei;
    public String serialNo;
    public String androidId;
}
