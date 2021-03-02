package ar.ne.rcs.shared.models.configs;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
public class CommunicationConfigModel {
    public String host;
    public int port;
    @Builder.Default
    public String deviceID = null;

    @Builder.Default
    public boolean httpSSL = false;
    public String httpEndpoint;

    @Builder.Default
    public boolean wsSSL = false;
    @Builder.Default
    public String wsEndpoint = "ws/websocket";


    public String getAddress() {
        return String.format("%s:%s", host, port);
    }

    public String getAPIBaseURL() {
        String protocol = "http";
        if (httpSSL) protocol += "s";
        return String.format("%s://%s/%s", protocol, getAddress(), httpEndpoint);
    }

    public String getWSUri() {
        return String.format("%s://%s:%s/%s", wsSSL ? "wss" : "ws", host, port, wsEndpoint);
    }
}
