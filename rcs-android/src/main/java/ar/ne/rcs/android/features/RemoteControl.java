package ar.ne.rcs.android.features;

import ar.ne.rcs.android.AndroidFeatureManager;
import ar.ne.rcs.client.feature.Feature;
import ar.ne.rcs.client.feature.FeatureConfigModel;
import ar.ne.rcs.client.feature.RemoteShell;
import ar.ne.rcs.client.utilities.shell.Executor;
import ar.ne.rcs.shared.models.frp.FRPClient;
import lombok.Builder;
import lombok.Getter;

//TODO: enable this
public class RemoteControl extends Feature<RemoteControl.RemoteControlConfigModel> {

    protected RemoteControl(AndroidFeatureManager manager, RemoteControlConfigModel configModel) {
        super(manager, configModel);
    }

    @Override
    public Class<? extends Feature<? extends FeatureConfigModel>> getFeatureType() {
        return RemoteControl.class;
    }

    public void listen(int port) {

    }

    public void startWirelessADB(int port) {
        Executor executor = manager.getFeature(RemoteShell.class).getExecutor();
        executor.exec("setprop service.adb.tcp.port " + port);
        executor.exec("stop adbd");
        executor.exec("start adbd");
    }


    @Getter
    @Builder
    public static class RemoteControlConfigModel extends FeatureConfigModel {
        FRPClient client;
        @Builder.Default
        int adbPort = 5555;
    }
}
