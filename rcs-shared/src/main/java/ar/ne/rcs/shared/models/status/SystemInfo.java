package ar.ne.rcs.shared.models.status;

import lombok.Data;

import java.io.Serializable;

@Data
public class SystemInfo implements Serializable {
    public String upTime;
    /**
     * 网络类型，2G 3G 4G WIFI
     */
    public String networkType;
    /**
     * 信号强度，0-10
     */
    public String connectionStrength;
    public String connectionSpeed;
    public String totalCallTime;
    public String xwtVersion;
}