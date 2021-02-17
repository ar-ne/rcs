package ar.ne.rcs.android.features;

import ar.ne.rcs.android.RCSAndroidManager;
import ar.ne.rcs.client.utilities.shell.Executor;
import lombok.Builder;

public class RemoteShell extends Feature<RemoteShell.RemoteShellConfigModel> {

    private Executor executor;

    protected RemoteShell(RCSAndroidManager manager, RemoteShellConfigModel configModel) {
        super(manager, configModel);
        try {
            executor = configModel.executorClass.newInstance();
        } catch (IllegalAccessException | InstantiationException e) {
            e.printStackTrace();
        }

        if (executor == null) throw new RuntimeException("Unable to instance executor");
    }

    public Executor getExecutor() {
        return executor;
    }

    @Override
    Class<? extends Feature<RemoteShellConfigModel>> getFeatureType() {
        return this.getClass();
    }

    @Builder
    public static class RemoteShellConfigModel extends FeatureConfigModel {
        Class<? extends Executor> executorClass;
    }
}
