package ar.ne.rcs.android;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.admin.DevicePolicyManager;
import android.content.ComponentName;
import android.content.Context;
import android.telephony.TelephonyManager;
import android.util.Log;
import ar.ne.rcs.android.shell.SuExecutor;
import ar.ne.rcs.client.RCSClient;
import ar.ne.rcs.client.utilities.shell.Result;
import ar.ne.rcs.shared.models.communication.ConfigModel;
import ar.ne.rcs.shared.models.status.DeviceStatus;

import java.util.concurrent.Callable;

import static android.content.Context.TELEPHONY_SERVICE;
import static ar.ne.rcs.client.RCSClient.RCS_CLIENT;

public class EMMClient {
    public static volatile EMMClient EMM_CLIENT = null;
    private final String APIBase;
    private final Class<? extends Receiver> clazz;

    private EMMClient(String apiBase, Class<? extends Receiver> clazz) {
        if (EMM_CLIENT != null) throw new RuntimeException("DO NOT INIT THIS TWICE");
        APIBase = apiBase;
        this.clazz = clazz;
    }

    public static EMMClient init(final Activity activity, Callable<DeviceStatus> collector, final String apiBase, Class<? extends Receiver> clazz) {
        EMM_CLIENT = new EMMClient(apiBase, clazz);
        new Thread(() -> EMM_CLIENT.StartRCSClient(activity, collector)).start();
        setDeviceOwner(activity, clazz);

        return EMM_CLIENT;
    }

    private static void setDeviceOwner(Activity activity, Class<? extends Receiver> clazz) {
        //set device owner
        Result sp = RCS_CLIENT.getExecutor().exec(String.format("su && dpm set-device-owner %s/%s", activity.getPackageName(), clazz.getName()));
        Log.i("EMM", sp.getOutput());
        DevicePolicyManager dpm = (DevicePolicyManager) activity.getSystemService(Context.DEVICE_POLICY_SERVICE);
        ComponentName dpmComp = new ComponentName(activity, clazz);
        dpm.setLockTaskPackages(dpmComp, new String[]{activity.getPackageName()});
        RuntimeConfig.activity = activity;
    }

    public void startLockTaskMode() {
        try {
            RuntimeConfig.setLocked(true);
            RuntimeConfig.activity.startLockTask();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void stopLockTaskMode() {
        try {
            RuntimeConfig.setLocked(false);
            RuntimeConfig.activity.stopLockTask();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * @param pwd      密码
     * @param runnable 回调
     * @return <span color=red>false</span> 密码正确执行回调之后返回<span color=green>true</span>密码错误
     */
    public boolean CheckPWDAndRun(String pwd, Runnable runnable) {
        if (chkPWD(pwd)) {
            runnable.run();
            return false;
        }
        return true;
    }

    /**
     * @param p 用户输入的密码
     * @return <span color=green>true</span> if correct,<span color=red>false</span> if incorrect
     */
    private boolean chkPWD(String p) {
        return RuntimeConfig.getPwd().contains(p);
    }

    @SuppressLint({"MissingPermission", "HardwareIds"})
    private void StartRCSClient(Activity activity, Callable<DeviceStatus> collector) {
        Log.i("EMMClient", "StartRCSClient: starting rcs client...");
        TelephonyManager telephonyManager = (TelephonyManager) activity.getSystemService(TELEPHONY_SERVICE);
        String IMEI = telephonyManager.getDeviceId();

        RCSClient.newInstance(
                new SuExecutor(),
                ConfigModel.builder()
                        .deviceID(IMEI)
                        .host("localhost")
                        .port(8080)
                        .httpEndpoint("")
                        .build()
        );
        AppWhitelist.apply(activity.getPackageManager());
    }
}
