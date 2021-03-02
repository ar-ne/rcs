package ar.ne.rcs.android.functions;

import android.content.Intent;
import android.net.Uri;
import android.os.StrictMode;
import ar.ne.rcs.android.AndroidFeatureManager;
import ar.ne.rcs.client.feature.RemoteShell;
import ar.ne.rcs.client.utilities.shell.Result;

import java.io.File;

import static ar.ne.rcs.client.feature.FeatureManager.MANAGER;

public class PkgManager {
    static {
        StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder();
        StrictMode.setVmPolicy(builder.build());
    }

    public static void install(File apkFile) {
        Intent intent = new Intent(Intent.ACTION_VIEW);
        // 由于没有在Activity环境下启动Activity,设置下面的标签
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        //参数1 上下文, 参数2 Provider主机地址 和配置文件中保持一致   参数3  共享的文件
        Uri apkUri = Uri.fromFile(apkFile);
        //添加这一句表示对目标应用临时授权该Uri所代表的文件
        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        intent.setDataAndType(apkUri, "application/vnd.android.package-archive");
        ((AndroidFeatureManager) MANAGER).getAppCtx().startActivity(intent);
    }

    public static int uninstall(String packageName) {
        Result result = MANAGER.getFeature(RemoteShell.class).getExecutor().exec("pm uninstall " + packageName);
        return result.getExitCode();
    }
}
