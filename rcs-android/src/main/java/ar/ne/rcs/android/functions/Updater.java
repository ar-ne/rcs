package ar.ne.rcs.android.functions;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.pm.PackageManager;
import ar.ne.rcs.shared.models.common.VersionInfo;
import ar.ne.rcs.shared.models.stores.VersionStore;

import java.io.File;


public class Updater {
    @SuppressWarnings("deprecation")
    public static int getVersionCode(Context mContext) {
        if (mContext != null) {
            try {
                return mContext.getPackageManager().getPackageInfo(mContext.getPackageName(), 0).versionCode;
            } catch (PackageManager.NameNotFoundException ignored) {
                ignored.printStackTrace();
            }
        }
        return 0;
    }

    @SuppressLint("DefaultLocale")
    public static void checkForNewVersion(Context mContext) throws PackageManager.NameNotFoundException {
        VersionStore serverVersion = new VersionStore(null, new VersionInfo());//TODO: use api to get server version code
        if (serverVersion.getVersionInfo().getRemoteVersion() > getVersionCode(mContext)) {
            String fileName = String.format("/data/local/tmp/%d.apk", System.currentTimeMillis());
            boolean downloaded = Downloader.download(serverVersion.getVersionInfo().getUrl(), fileName);
            if (downloaded) AppManager.install(new File(fileName), mContext);
        }
    }

}
