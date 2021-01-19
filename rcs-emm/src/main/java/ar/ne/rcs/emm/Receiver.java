package ar.ne.rcs.emm;

import android.app.admin.DeviceAdminReceiver;
import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

public abstract class Receiver extends DeviceAdminReceiver {
    @Override
    public void onLockTaskModeExiting(Context context, Intent intent) {
        if (Config.isLocked()) {
            context.startActivity(new Intent(context, Config.activity.getClass()).setFlags(Intent.FLAG_ACTIVITY_NEW_TASK));
            Toast.makeText(context, "YOU SHELL NOT PASS!", Toast.LENGTH_SHORT).show();
        }
    }
}
