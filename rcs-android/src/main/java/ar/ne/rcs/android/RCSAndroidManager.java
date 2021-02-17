package ar.ne.rcs.android;

import android.app.Activity;
import android.content.pm.PackageManager;
import ar.ne.rcs.android.features.*;
import ar.ne.rcs.android.shell.SuExecutor;
import ar.ne.rcs.shared.models.configs.RCSAndroidConfigModel;

import java.lang.reflect.InvocationTargetException;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;

public class RCSAndroidManager {
    public static volatile RCSAndroidManager MANAGER;
    final RCSAndroidConfigModel config;

    public RCSAndroidManager(RCSAndroidConfigModel config) {
        if (MANAGER != null) throw new RuntimeException("DO NOT　construct manager multiple time");
        this.config = config;
        MANAGER = this;
        enableFeature(
                RemoteShell.class,
                RemoteShell.RemoteShellConfigModel.builder()
                        .executorClass(SuExecutor.class)
                        .build()
        );
    }

    public <T extends Feature<? extends FeatureConfigModel>> T getFeature(Class<T> featureType) {
        return Feature.get(featureType);
    }

    public <T extends FeatureConfigModel> void enableFeature(Class<? extends Feature<? extends FeatureConfigModel>> feature, T config) {
        try {
            feature.getConstructor(RCSAndroidManager.class, FeatureConfigModel.class)
                    .newInstance(this, config);
        } catch (InstantiationException | IllegalAccessException | NoSuchMethodException | InvocationTargetException e) {
            e.printStackTrace();
        }
    }

    public void enableEnterpriseManagement(Class<? extends Activity> restartActivity, String... passwords) {
        enableFeature(
                EnterpriseManagement.class,
                EnterpriseManagement.EnterpriseManagementConfigModel.builder()
                        .restartActivity(restartActivity)
                        .passwords(passwords.length == 0 ? new HashSet<>() : new HashSet<>(Arrays.asList(passwords)))
                        .build()
        );
    }

    public void enableAppWhitelist(Activity activity, PackageManager packageManager) {
        enableFeature(
                AppWhitelist.class,
                AppWhitelist.AppWhitelistConfigModel.builder()
                        .packageManager(packageManager)
                        .allowedPkg(new HashSet<>(Collections.singletonList(activity.getPackageName())))
                        .reboot(true)
                        .build()
        );
    }

    //TODO: enable other features
}
