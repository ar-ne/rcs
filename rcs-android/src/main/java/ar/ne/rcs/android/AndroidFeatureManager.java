package ar.ne.rcs.android;

import android.app.Activity;
import android.content.pm.PackageManager;
import ar.ne.rcs.android.features.AppWhitelist;
import ar.ne.rcs.android.features.EnterpriseManagement;
import ar.ne.rcs.android.shell.SuExecutor;
import ar.ne.rcs.android.utils.SPAConnection;
import ar.ne.rcs.client.RCSClient;
import ar.ne.rcs.client.feature.Feature;
import ar.ne.rcs.client.feature.FeatureConfigModel;
import ar.ne.rcs.client.feature.FeatureManager;
import ar.ne.rcs.client.feature.RemoteShell;
import ar.ne.rcs.shared.models.configs.RCSAndroidConfigModel;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;

public class AndroidFeatureManager extends FeatureManager {
    final RCSAndroidConfigModel config;

    public AndroidFeatureManager(RCSAndroidConfigModel config) {
        if (MANAGER != null) throw new RuntimeException("DO NOT　construct manager multiple time");
        this.config = config;
        MANAGER = this;
        enableFeature(
                RemoteShell.class,
                RemoteShell.RemoteShellConfigModel.builder()
                        .executorClass(SuExecutor.class)
                        .build()
        );

        //connect to server
        RCSClient client = new RCSClient(
                config.getCommunicationConfig(),
                new SPAConnection(config.getCommunicationConfig().getWSUri())
        );
    }

    @Override
    public <T extends Feature<? extends FeatureConfigModel>> T getFeature(Class<T> featureType) {
        return Feature.get(featureType);
    }

    @Override
    public <T extends FeatureConfigModel> void enableFeature(Class<? extends Feature<? extends FeatureConfigModel>> feature, T config) {
        try {
            Constructor<?>[] constructors = feature.getDeclaredConstructors();
            if (constructors.length != 1)
                throw new RuntimeException("No wanted constructor found");
            Constructor<?> constructor = constructors[0];
            constructor.setAccessible(true);
            constructor.newInstance(this, config);
            constructor.setAccessible(false);
        } catch (InstantiationException | IllegalAccessException | InvocationTargetException e) {
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
