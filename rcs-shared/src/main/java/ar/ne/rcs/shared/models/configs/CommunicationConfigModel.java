package ar.ne.rcs.shared.models.configs;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class CommunicationConfigModel {
    public String host;
    public int port;
    public String deviceID;

    @Builder.Default
    public boolean httpSSL = false;
    public String httpEndpoint;

    @Builder.Default
    public boolean wsSSL = false;
    public String wsEndpoint;


    public String getAddress() {
        return String.format("%s:%s", host, port);
    }

    public String getAPIBaseURL() {
        String protocol = "http";
        if (httpSSL) protocol += "s";
        return String.format("%s://%s/%s", protocol, getAddress(), httpEndpoint);
    }
}
