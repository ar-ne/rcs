package ar.ne.rcs.shared.models.status;

import lombok.Data;

import java.io.Serializable;

@Data
public class DeviceStatus implements Serializable {
    public String os_version;
    public SystemInfo systemInfo;
    public EnvironmentInfo environmentInfo;
}
