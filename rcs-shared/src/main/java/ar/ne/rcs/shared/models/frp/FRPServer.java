package ar.ne.rcs.shared.models.frp;

import ar.ne.rcs.shared.interfaces.Ini;
import lombok.Builder;
import lombok.Data;

import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

@Data
@Builder
public class FRPServer implements Ini {
    public Common common;

    @Override
    public String toIni() {
        org.ini4j.Ini ini = new org.ini4j.Ini();
        org.ini4j.Profile.Section section1 = ini.add("common");
        section1.add("tls_only", common.tls_only);
        section1.add("bind_port", common.bind_port);
        section1.add("bind_addr", common.bind_addr);
        section1.add("allow_ports", common.allow_ports);
        section1.add("dashboard_addr", common.dashboard_addr);
        section1.add("dashboard_port", common.dashboard_port);
        section1.add("authentication_method", common.authentication_method);
        section1.add("token", common.token);


        ByteArrayOutputStream a = new ByteArrayOutputStream();
        BufferedOutputStream stream = new BufferedOutputStream(a);
        try {
            ini.store(stream);
            return a.toString();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Data
    @Builder
    public static class Common {
        @Builder.Default
        public boolean tls_only = true;
        @Builder.Default
        public int bind_port = 20545;
        @Builder.Default
        public String bind_addr = "0.0.0.0";
        @Builder.Default
        public String allow_ports = "40000-50000";
        @Builder.Default
        public String dashboard_addr = "0.0.0.0";
        @Builder.Default
        public int dashboard_port = 27609;
        @Builder.Default
        public String authentication_method = "token";
        public String token;
    }
}
