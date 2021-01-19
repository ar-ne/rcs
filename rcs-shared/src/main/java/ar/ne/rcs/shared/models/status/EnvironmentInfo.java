package ar.ne.rcs.shared.models.status;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

@Data
public class EnvironmentInfo implements Serializable {
    /**
     * 已安装的包列表(包名，版本)
     */
    public List<AppPackage> packages;

    @Data
    @AllArgsConstructor
    public static class AppPackage implements Serializable {
        public String name;
        public String version;
        public Object applicationInfo;
    }
}

