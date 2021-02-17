package ar.ne.rcs.client.communication.http;

import ar.ne.rcs.client.communication.HttpConfig;
import ar.ne.rcs.shared.models.RemoteCommand;
import ar.ne.rcs.shared.models.status.DeviceStatus;

import java.io.IOException;

public class HandyRequests {
    private static String getUrl(String endpoint) throws RuntimeException {
        return String.format("%s/%s", HttpConfig.getInstance().baseUrl, endpoint);
    }

    public static int GetInterval() throws IOException {
        return Integer.parseInt(HTTPRequest.GetString(getUrl(HTTPAPIConst.interval), null));
    }

    public static RemoteCommand[] CheckRemoteCommandUpdate() throws IOException {
        return HTTPRequest.GetObject(getUrl(HTTPAPIConst.job_list), null, RemoteCommand[].class);
    }

    public static void UpdateRemoteCommandResult(Object data) throws IOException {
        HTTPRequest.PostObject(getUrl(HTTPAPIConst.job_update), data);
    }

    public static void uploadDeviceStatus(DeviceStatus ds) throws IOException {
        HTTPRequest.PostObject(getUrl(HTTPAPIConst.StatusUpload), ds);
    }
}
