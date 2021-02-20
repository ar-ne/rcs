package ar.ne.rcs.android;

import android.app.admin.DeviceAdminReceiver;
import android.content.Context;
import android.content.Intent;
import ar.ne.rcs.android.features.EnterpriseManagement;

import static ar.ne.rcs.android.AndroidFeatureManager.MANAGER;

public abstract class DPMReceiver extends DeviceAdminReceiver {
    @Override
    public void onLockTaskModeExiting(Context context, Intent intent) {
        if (EnterpriseManagement.RuntimeStatus.isLocked()) {
            context.startActivity(
                    new Intent(
                            context,
                            MANAGER.getFeature(EnterpriseManagement.class).getConfig().getRestartActivity()
                    ).setFlags(Intent.FLAG_ACTIVITY_NEW_TASK));
        }
    }
}
