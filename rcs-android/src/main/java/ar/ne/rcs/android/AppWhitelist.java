package ar.ne.rcs.android;


import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import ar.ne.rcs.client.utilities.shell.Executor;

import java.util.Arrays;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;

import static ar.ne.rcs.client.RCSClient.RCS_CLIENT;

public class AppWhitelist {
    private final static String[] list = (
            "com.android.cts.priv.ctsshim\n" +
                    "com.android.providers.telephony\n" +
                    "com.android.providers.media\n" +
                    "com.license.type\n" +
                    "com.android.providers.downloads\n" +
                    "com.example.administrator.floatwindow\n" +
                    "com.android.defcontainer\n" +
                    "com.android.providers.downloads.ui\n" +
                    "com.android.pacprocessor\n" +
                    "com.android.certinstaller\n" +
                    "android\n" +
                    "com.android.provision\n" +
                    "com.android.statementservice\n" +
                    "com.android.apkinstaller\n" +
                    "com.android.providers.settings\n" +
                    "com.android.webview\n" +
                    "com.android.inputdevices\n" +
                    "com.smdt.settings_gpio\n" +
                    "com.android.musicfx\n" +
                    "com.google.android.webview\n" +
                    "android.ext.shared\n" +
                    "com.android.onetimeinitializer\n" +
                    "com.android.server.telecom\n" +
                    "com.android.keychain\n" +
                    "com.smdt.RecordLogforSettings\n" +
                    "android.ext.services\n" +
                    "com.android.packageinstaller\n" +
                    "com.android.proxyhandler\n" +
                    "com.android.inputmethod.latin\n" +
                    "com.android.settings\n" +
                    "com.android.phone\n" +
                    "com.android.shell\n" +
                    "com.android.location.fused\n" +
                    "com.android.systemui\n" +
                    "com.android.providers.contacts\n" +
                    "com.android.captiveportallogin\n" +
                    "com.school.klass\n" +
                    "android\n" +
                    "com.smdt.settings_gpio\n" +
                    "com.android.managedprovisioning\n" +
                    "com.android.carrierconfig\n" +
                    "com.android.inputservice\n" +
                    "com.smdt.timing\n").split("\n");

    public static void apply(PackageManager packageManager) {
        HashSet<PackageInfo> pkgToDel = new HashSet<>();
        HashSet<String> pkgAllowed = new HashSet<>(Arrays.asList(list));
        List<PackageInfo> packages = packageManager.getInstalledPackages(0);
        for (PackageInfo info : packages) {
            if (!pkgAllowed.contains(info.packageName)) {
                pkgToDel.add(info);
            }
        }

        if (pkgToDel.isEmpty()) return;

        HashSet<String> dirs = new HashSet<>();
        for (PackageInfo packageInfo : pkgToDel) {
            ApplicationInfo appInfo = packageInfo.applicationInfo;
            dirs.add(appInfo.publicSourceDir);
            dirs.add(appInfo.sourceDir);
        }

        List<String> cmdList = new LinkedList<>();

        for (String dir : dirs) {
            cmdList.add("rm -rf " + dir);
        }
        cmdList.add("reboot");

        StringBuilder builder = new StringBuilder();
        for (String s : cmdList) {
            builder.append(s).append("\n");
        }

        Executor executor = RCS_CLIENT.getExecutor();
        executor.exec("mount -o rw,remount /system");
        for (String s : cmdList) executor.exec(s);
        executor.exec("mount -o rw,remount /system");
    }
}
