package ar.ne.rcs.client.network.http;

import ar.ne.rcs.client.network.APIConfig;
import ar.ne.rcs.client.network.Config;
import ar.ne.rcs.shared.models.RemoteCommand;
import ar.ne.rcs.shared.models.status.DeviceStatus;

public class HandyRequests {
    private static String getUrl(String endpoint) throws Exception {
        return String.format("%s/%s", Config.getInstance().baseUrl, endpoint);
    }

    public static int GetInterval() throws Exception {
        return Integer.parseInt(HTTPRequest.GetString(getUrl(APIConfig.interval), null));
    }

    public static RemoteCommand[] CheckRemoteCommandUpdate() throws Exception {
        return HTTPRequest.GetObject(getUrl(APIConfig.job_list), null, RemoteCommand[].class);
    }

    public static void UpdateRemoteCommandResult(Object data) throws Exception {
        HTTPRequest.PostObject(getUrl(APIConfig.job_update), data);
    }

    public static void uploadDeviceStatus(DeviceStatus ds) throws Exception {
        HTTPRequest.PostObject(getUrl(APIConfig.StatusUpload), ds);
    }
}
