package ar.ne.rcs.android;

import android.app.admin.DeviceAdminReceiver;
import android.content.Context;
import android.content.Intent;

public abstract class Receiver extends DeviceAdminReceiver {
    @Override
    public void onLockTaskModeExiting(Context context, Intent intent) {
        if (RuntimeConfig.isLocked()) {
            context.startActivity(new Intent(context, RuntimeConfig.activity.getClass()).setFlags(Intent.FLAG_ACTIVITY_NEW_TASK));
        }
    }
}
