package ar.ne.rcs.android.utils;

import android.annotation.SuppressLint;
import android.content.Context;
import android.telephony.TelephonyManager;
import ar.ne.rcs.android.AndroidFeatureManager;
import ar.ne.rcs.client.feature.RemoteShell;
import ar.ne.rcs.client.utilities.shell.Result;

import static ar.ne.rcs.client.feature.FeatureManager.MANAGER;

public class DeviceIdentifier {
    private static String cachedIdentifier = null;

    public static String getIdentifier() {
        if (cachedIdentifier == null)
            cachedIdentifier = generateIdentifier(((AndroidFeatureManager) MANAGER).getAppCtx());
        return cachedIdentifier;
    }

    private static String generateIdentifier(Context context) {
        String imei = getIMEI(context);
        String serial = getSerialNo();

        if (imei == null) return serial;
        return imei;
    }

    @SuppressWarnings("deprecation")
    private static String getIMEI(Context context) {
        TelephonyManager systemService = context.getSystemService(TelephonyManager.class);
        @SuppressLint({"MissingPermission", "HardwareIds"}) String deviceId = systemService.getDeviceId();
        if (deviceId != null && !deviceId.trim().isEmpty()) return deviceId;
        return null;
    }

    private static String getSerialNo() {
        return getResult(MANAGER.getFeature(RemoteShell.class).getExecutor().exec("getprop ro.serialno"));
    }

    private static String getResult(Result result) {
        if (result.getExitCode() != 0) return null;
        if (result.getOutput().trim().isEmpty()) return null;
        return result.getOutput().trim();
    }
}
