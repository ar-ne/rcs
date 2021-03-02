package ar.ne.rcs.shared.models.devices;

import ar.ne.rcs.shared.models.stores.VersionStore;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DeviceInfo {
    /**
     * 是否正在使用，未使用时，设备列表的故障设备中不显示该设备
     */
    boolean enabled;
    /**
     * 位置：安装位置或当前位置
     */
    String address;
    /**
     * 设备类型：大班牌或小话机
     */
    String type;
    /**
     * school name
     */
    String school;
    /**
     * combined grade and class, e.g. 2020级3班
     */
    String gradeAndClass;
    /**
     * device identifier
     */
    String id;
    /**
     * device's version group, ref {@link VersionStore#getId()}
     */
    String versionGroup;
}
