package ar.ne.rcs.emm;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.admin.DevicePolicyManager;
import android.content.ComponentName;
import android.content.Context;
import android.os.Build;
import android.telephony.TelephonyManager;
import android.util.Log;
import ar.ne.rcs.client.RCSClientMain;
import ar.ne.rcs.shared.models.status.DeviceStatus;
import ar.ne.rcs.shared.subprocess.SubProcess;

import java.io.FileWriter;
import java.io.IOException;
import java.util.concurrent.Callable;

import static android.content.Context.TELEPHONY_SERVICE;

public class EMMClient {
    private static EMMClient client;
    private final String APIBase;
    private final Class<? extends Receiver> clazz;

    private EMMClient(String apiBase, Class<? extends Receiver> clazz) {
        APIBase = apiBase;
        this.clazz = clazz;
    }

    public static EMMClient Config(final Activity activity, Callable<DeviceStatus> collector, final String apiBase, Class<? extends Receiver> clazz) {
        if (client == null) {
            client = new EMMClient(apiBase, clazz);
            new Thread(() -> client.StartRCSClient(activity, collector)).start();

            //set device owner
            SubProcess sp = new SubProcess(String.format("su && dpm set-device-owner %s/%s", activity.getPackageName(), clazz.getName()));
            Log.i("EMM", sp.getOutput());
            DevicePolicyManager dpm = (DevicePolicyManager) activity.getSystemService(Context.DEVICE_POLICY_SERVICE);
            ComponentName dpmComp = new ComponentName(activity, clazz);
            dpm.setLockTaskPackages(dpmComp, new String[]{activity.getPackageName()});
            Config.activity = activity;
        }

        return client;
    }

    public static EMMClient getClient() {
        if (client == null) {
            throw new NullPointerException("Config EMMClient first! use EMMClient.Config(...)");
        }
        return client;
    }

    public void startLockTaskMode() {
        try {
            Config.setLocked(true);
            Config.activity.startLockTask();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void stopLockTaskMode() {
        try {
            Config.setLocked(false);
            Config.activity.stopLockTask();
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
        return Config.getPwd().contains(p);
    }

    @SuppressLint({"MissingPermission", "HardwareIds"})
    private void StartRCSClient(Activity activity, Callable<DeviceStatus> collector) {
        Log.i("EMMClient", "StartRCSClient: starting rcs client...");
        releaseScript();
        TelephonyManager telephonyManager = (TelephonyManager) activity.getSystemService(TELEPHONY_SERVICE);

        String IMEI = telephonyManager.getDeviceId();
        while (true) {
            RCSClientMain.newInstance(
                    APIBase,
                    IMEI,
                    collector
            );
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    private void releaseScript() {
        String removeLauncher3 = "\n";
        if (Build.VERSION.RELEASE.equals("5.1.1"))
            removeLauncher3 = "rm -rf /system/app/Launcher3\n";
        //释放removeAPK.sh到 /sdcard/rk.sh
        String rk = "echo \"Trying remount system rw\"\n" +
                "mount -o rw,remount /system\n" +
                "echo \"remount success?\"\n" +
                "\n" +
                "echo \"rm files\"\n" +
                "rm -rf /system/app/messaging\n" +
                "rm -rf /system/app/Bluetooth\n" +
                "rm -rf /system/app/BluetoothMidiService\n" +
                "rm -rf /system/app/GooglePinyinIME\n" +
                "rm -rf /system/app/Gallery2\n" +
                "rm -rf /system/priv-app/Mms\n" +
                "rm -rf /system/priv-app/Contacts\n" +
                removeLauncher3 +
                "echo \"delete done\"\n" +
                "echo \"rebooting...\"\n" +
                "reboot";

        String frpc_ini = "[common]\n" +
                "token = 这是个token,客户端和服务器要一致\n" +
                "tls_enable = true\n" +
                "\n" +
                "server_addr = 118.190.107.32\n" +
                "server_port = 20545\n" +
                "\n" +
                "authentication_method = token\n" +
                "\n" +
                "[wadb]\n" +
                "type = tcp\n" +
                "local_port = 5555\n" +
                "remote_port = 0";
        try {
            FileWriter fileWriter = new FileWriter("/sdcard/rk.sh", false);
            fileWriter.write(rk);
            fileWriter.flush();
            fileWriter.close();
            fileWriter = new FileWriter("/sdcard/frpc.ini", false);
            fileWriter.write(rk);
            fileWriter.flush();
            fileWriter.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
