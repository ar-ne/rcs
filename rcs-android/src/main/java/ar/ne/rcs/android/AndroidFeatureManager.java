package ar.ne.rcs.android;

import android.app.Activity;
import android.content.Context;
import ar.ne.rcs.android.features.AppWhitelist;
import ar.ne.rcs.android.features.EnterpriseManagement;
import ar.ne.rcs.android.features.RemoteControl;
import ar.ne.rcs.android.shell.SuExecutor;
import ar.ne.rcs.android.utils.DeviceIdentifier;
import ar.ne.rcs.android.utils.SPAConnection;
import ar.ne.rcs.client.RCSClient;
import ar.ne.rcs.client.feature.*;
import ar.ne.rcs.shared.models.configs.RCSAndroidConfigModel;
import lombok.Getter;
import lombok.extern.java.Log;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;

@Log
public class AndroidFeatureManager extends FeatureManager {
    @Getter
    final Context appCtx;
    final RCSAndroidConfigModel config;

    private AndroidFeatureManager(Context context, RCSAndroidConfigModel config) {
        if (MANAGER != null) throw new RuntimeException("DO NOT initialize manager multiple time");
        appCtx = context.getApplicationContext();
        this.config = config;
        MANAGER = this;
        enableFeature(
                RemoteShell.class,
                RemoteShell.RemoteShellConfigModel.builder()
                        .executorClass(SuExecutor.class)
                        .build()
        );
        enableFeature(
                PredefinedFunctions.class,
                new PredefinedFunctions.PredefinedFunctionsConfigModel()
        );
        enableFeature(RemoteControl.class, RemoteControl.RemoteControlConfigModel.builder().build());

        if (config != null) {
            //connect to server
            RCSClient client = new RCSClient(
                    config.getCommunicationConfig(),
                    new SPAConnection(config.getCommunicationConfig().getWSUri())
            );
        } else {
            log.warning("communication disabled");
        }
    }

    public static AndroidFeatureManager init(Context ctx, RCSAndroidConfigModel config) {
        if (config != null)
            config.setCommunicationConfig(config.getCommunicationConfig().toBuilder().deviceID(DeviceIdentifier.getIdentifier()).build());
        return new AndroidFeatureManager(ctx, config);
    }

    public static AndroidFeatureManager init(Context ctx) {
        return new AndroidFeatureManager(ctx, null);
    }

    @Override
    public <T extends Feature<? extends FeatureConfigModel>> T getFeature(Class<T> featureType) {
        return Feature.get(featureType);
    }

    @Override
    public <T extends FeatureConfigModel> AndroidFeatureManager enableFeature(Class<? extends Feature<? extends FeatureConfigModel>> feature, T config) {
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
        return this;
    }

    public AndroidFeatureManager enableEnterpriseManagement(Class<? extends Activity> restartActivity, String... passwords) {
        return enableFeature(
                EnterpriseManagement.class,
                EnterpriseManagement.EnterpriseManagementConfigModel.builder()
                        .restartActivity(restartActivity)
                        .passwords(passwords.length == 0 ? new HashSet<>() : new HashSet<>(Arrays.asList(passwords)))
                        .build()
        );
    }

    public AndroidFeatureManager enableAppWhitelist(Activity activity) {
        return enableFeature(
                AppWhitelist.class,
                AppWhitelist.AppWhitelistConfigModel.builder()
                        .packageManager(activity.getPackageManager())
                        .allowedPkg(new HashSet<>(Collections.singletonList(activity.getPackageName())))
                        .reboot(true)
                        .build()
        );
    }

    //TODO: enable other features
}
