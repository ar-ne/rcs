package ar.ne.rcs.android.features;


import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import ar.ne.rcs.android.RCSAndroidManager;
import ar.ne.rcs.client.utilities.shell.Executor;
import lombok.Builder;
import lombok.Getter;

import java.util.*;

import static ar.ne.rcs.client.RCSClient.RCS_CLIENT;

public class AppWhitelist extends Feature<AppWhitelist.AppWhitelistConfigModel> {
    private final static HashSet<String> INTERNAL_LIST =
            new HashSet<>(
                    Arrays.asList(
                            "com.android.cts.priv.ctsshim",
                            "com.android.providers.telephony",
                            "com.android.providers.media",
                            "com.license.type",
                            "com.android.providers.downloads",
                            "com.example.administrator.floatwindow",
                            "com.android.defcontainer",
                            "com.android.providers.downloads.ui",
                            "com.android.pacprocessor",
                            "com.android.certinstaller",
                            "android",
                            "com.android.provision",
                            "com.android.statementservice",
                            "com.android.apkinstaller",
                            "com.android.providers.settings",
                            "com.android.webview",
                            "com.android.inputdevices",
                            "com.smdt.settings_gpio",
                            "com.android.musicfx",
                            "com.google.android.webview",
                            "android.ext.shared",
                            "com.android.onetimeinitializer",
                            "com.android.server.telecom",
                            "com.android.keychain",
                            "com.smdt.RecordLogforSettings",
                            "android.ext.services",
                            "com.android.packageinstaller",
                            "com.android.proxyhandler",
                            "com.android.inputmethod.latin",
                            "com.android.settings",
                            "com.android.phone",
                            "com.android.shell",
                            "com.android.location.fused",
                            "com.android.systemui",
                            "com.android.providers.contacts",
                            "com.android.captiveportallogin",
                            "com.school.klass",
                            "android",
                            "com.smdt.settings_gpio",
                            "com.android.managedprovisioning",
                            "com.android.carrierconfig",
                            "com.android.inputservice",
                            "com.smdt.timing"));

    protected AppWhitelist(RCSAndroidManager manager, AppWhitelistConfigModel configModel) {
        super(manager, configModel);
        configModel.getAllowedPkg().addAll(INTERNAL_LIST);

        HashSet<PackageInfo> disallowedPkg = getDisallowedPkg();
        HashSet<String> files = new HashSet<>();
        disallowedPkg.stream().map(this::getPackageFile).forEach(files::addAll);
        applyDelete(convertToCommand(files, configModel.isReboot()));
    }

    public HashSet<PackageInfo> getDisallowedPkg() {
        HashSet<PackageInfo> pkgToDel = new HashSet<>();
        List<PackageInfo> packages = configModel.packageManager.getInstalledPackages(0);
        for (PackageInfo info : packages) {
            if (!configModel.getAllowedPkg().contains(info.packageName)) {
                pkgToDel.add(info);
            }
        }
        return pkgToDel;
    }

    private HashSet<String> getPackageFile(PackageInfo packageInfo) {
        HashSet<String> files = new HashSet<>();
        ApplicationInfo appInfo = packageInfo.applicationInfo;
        files.add(appInfo.publicSourceDir);
        files.add(appInfo.sourceDir);
        return files;
    }

    private List<String> convertToCommand(Set<String> fileToDelete, boolean reboot) {
        ArrayList<String> cmdList = new ArrayList<>();
        cmdList.add("mount -o rw,remount /system");
        for (String file : fileToDelete) {
            cmdList.add("rm -rf " + file);
        }
        if (reboot) {
            cmdList.add("reboot");
        }
        return cmdList;
    }

    public void applyDelete(List<String> commandList) {
        Executor executor = RCS_CLIENT.getExecutor();
        for (String s : commandList) executor.exec(s);
    }

    @Override
    Class<? extends Feature<? extends FeatureConfigModel>> getFeatureType() {
        return this.getClass();
    }

    @Getter
    @Builder
    public static class AppWhitelistConfigModel extends FeatureConfigModel {
        PackageManager packageManager;
        Set<String> allowedPkg;
        /**
         * {@code true} if you want reboot after finish file deletion
         */
        @Builder.Default
        boolean reboot = true;
    }
}
