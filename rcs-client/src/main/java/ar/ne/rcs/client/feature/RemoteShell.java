package ar.ne.rcs.client.feature;

import ar.ne.rcs.client.utilities.shell.Executor;
import lombok.Builder;

import java.lang.reflect.InvocationTargetException;

public class RemoteShell extends Feature<RemoteShell.RemoteShellConfigModel> {

    private Executor executor;

    protected RemoteShell(FeatureManager manager, RemoteShellConfigModel configModel) {
        super(manager, configModel);
        try {
            executor = configModel.executorClass.getConstructor().newInstance();
        } catch (IllegalAccessException | InstantiationException | NoSuchMethodException | InvocationTargetException e) {
            e.printStackTrace();
        }

        if (executor == null) throw new RuntimeException("Unable to instance executor");
    }

    public Executor getExecutor() {
        return executor;
    }

    @Override
    public Class<? extends Feature<RemoteShellConfigModel>> getFeatureType() {
        return RemoteShell.class;
    }

    @Builder
    public static class RemoteShellConfigModel extends FeatureConfigModel {
        Class<? extends Executor> executorClass;
    }
}
