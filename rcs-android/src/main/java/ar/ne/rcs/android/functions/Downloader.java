package ar.ne.rcs.android.functions;

import ar.ne.rcs.client.feature.RemoteShell;
import ar.ne.rcs.client.utilities.shell.Result;

import static ar.ne.rcs.client.feature.FeatureManager.MANAGER;

public class Downloader {

    public static boolean download(String url, String output) {
        Result r = MANAGER.getFeature(RemoteShell.class).getExecutor().exec(String.format("wget %s -O %s", url, output));
        return r.getExitCode() == 0;
    }
}
