package ar.ne.rcs.android.features;

import android.app.Activity;
import android.app.admin.DevicePolicyManager;
import android.content.ComponentName;
import android.content.Context;
import ar.ne.rcs.android.AndroidFeatureManager;
import ar.ne.rcs.android.DPMReceiver;
import ar.ne.rcs.client.feature.Feature;
import ar.ne.rcs.client.feature.FeatureConfigModel;
import ar.ne.rcs.client.feature.RemoteShell;
import ar.ne.rcs.client.utilities.shell.Result;
import lombok.Builder;
import lombok.Getter;
import lombok.extern.java.Log;

import java.util.HashSet;
import java.util.concurrent.atomic.AtomicBoolean;

@Log
public class EnterpriseManagement extends Feature<EnterpriseManagement.EnterpriseManagementConfigModel> {

    protected EnterpriseManagement(AndroidFeatureManager manager, EnterpriseManagementConfigModel enterpriseManagementConfigModel) {
        super(manager, enterpriseManagementConfigModel);
    }

    /**
     * Set current package to device-owner use {@code root} and {@code dpm set-device-owner}
     *
     * @param activity the activity instance for {@link DevicePolicyManager#setLockTaskPackages(ComponentName, String[])}'s {@link ComponentName}
     * @param clazz    your receiver extended {@link DPMReceiver}
     */
    public void setDeviceOwner(Activity activity, Class<? extends DPMReceiver> clazz) {
        Result sp = manager.getFeature(RemoteShell.class).getExecutor().exec(String.format("su && dpm set-device-owner %s/%s", activity.getPackageName(), clazz.getName()));
        log.info(sp.getOutput());
        DevicePolicyManager dpm = (DevicePolicyManager) activity.getSystemService(Context.DEVICE_POLICY_SERVICE);
        ComponentName dpmComp = new ComponentName(activity, clazz);
        dpm.setLockTaskPackages(dpmComp, new String[]{activity.getPackageName()});
    }

    @Override
    public Class<? extends Feature<EnterpriseManagementConfigModel>> getFeatureType() {
        return EnterpriseManagement.class;
    }

    public void startLockTaskMode(Activity activity) {
        try {
            EnterpriseManagement.RuntimeStatus.setLocked(true);
            activity.startLockTask();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void stopLockTaskMode(Activity activity) {
        try {
            EnterpriseManagement.RuntimeStatus.setLocked(false);
            activity.stopLockTask();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Getter
    @Builder
    public static class EnterpriseManagementConfigModel extends FeatureConfigModel {
        private final HashSet<String> passwords;
        /**
         * the activity to start when app exit lock task mode unexpect
         */
        private final Class<? extends Activity> restartActivity;
    }

    public static class RuntimeStatus {
        private static final AtomicBoolean deviceLocked = new AtomicBoolean(false);

        public static boolean isLocked() {
            return deviceLocked.get();
        }

        public static void setLocked(boolean locked) {
            deviceLocked.set(locked);
        }
    }
}
