package ar.ne.rcs.shared.models.frp;

import ar.ne.rcs.shared.models.IniConfig;
import lombok.Builder;
import lombok.Data;
import org.ini4j.Ini;

import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

@Data
@Builder
public class FRPClientConfig implements IniConfig {
    public String name;
    public Common common;
    public Profile profile;

    @Override
    public String toIni() {
        Ini ini = new Ini();
        org.ini4j.Profile.Section section1 = ini.add("common");
        section1.add("token", common.token);
        section1.add("authentication_method", common.authentication_method);
        section1.add("tls_enable", common.tls_enable);
        section1.add("server_addr", common.server_addr);
        section1.add("server_port", common.server_port);
        org.ini4j.Profile.Section section2 = ini.add(name);
        section2.add("type", profile.type);
        section2.add("local_port", profile.local_port);
        section2.add("remote_port", profile.remote_port);


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
        public String token;
        @Builder.Default
        public String authentication_method = "token";
        @Builder.Default
        public boolean tls_enable = true;
        public String server_addr;
        public int server_port;
    }

    @Data
    @Builder
    public static class Profile {
        @Builder.Default
        public String type = "tcp";
        @Builder.Default
        public int local_port = 5555;
        @Builder.Default
        public int remote_port = 0;
        @Builder.Default
        public boolean use_encryption = true;
        @Builder.Default
        public boolean use_compression = true;
    }
}
