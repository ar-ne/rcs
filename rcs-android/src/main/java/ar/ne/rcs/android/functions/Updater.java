package ar.ne.rcs.android.functions;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.pm.PackageManager;
import ar.ne.rcs.android.AndroidFeatureManager;
import ar.ne.rcs.shared.models.common.VersionInfo;
import ar.ne.rcs.shared.models.stores.VersionStore;

import java.io.File;

import static ar.ne.rcs.client.feature.FeatureManager.MANAGER;


public class Updater {
    @SuppressWarnings("deprecation")
    public static int getVersionCode() {
        Context ctx= ((AndroidFeatureManager) MANAGER).getAppCtx();
        try {
            return ctx.getPackageManager().getPackageInfo(ctx.getPackageName(), 0).versionCode;
        } catch (PackageManager.NameNotFoundException ignored) {
        }
        return 0;
    }

    @SuppressLint("DefaultLocale")
    public static void checkForNewVersion(){
        VersionStore serverVersion = new VersionStore(null, new VersionInfo());//TODO: use api to get server version code
        if (serverVersion.getVersionInfo().getRemoteVersion() > getVersionCode()) {
            String fileName = String.format("/data/local/tmp/%d.apk", System.currentTimeMillis());
            boolean downloaded = Downloader.download(serverVersion.getVersionInfo().getUrl(), fileName);
            if (downloaded) PkgManager.install(new File(fileName));
        }
    }

}
