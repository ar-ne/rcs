package ar.ne.rcs.client.feature;

import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;

public class PredefinedFunctions extends Feature<PredefinedFunctions.PredefinedFunctionsConfigModel> {

    private static final ConcurrentHashMap<String, Function<Object, Void>> functions = new ConcurrentHashMap<>();

    protected PredefinedFunctions(FeatureManager manager, PredefinedFunctionsConfigModel configModel) {
        super(manager, configModel);
    }

    public void putFunction(String name, Function<Object, Void> function) {
        functions.put(name, function);
    }

    public void callFunction(String name, Object arg) {
        functions.get(name).apply(arg);
    }

    @Override
    public Class<? extends Feature<? extends FeatureConfigModel>> getFeatureType() {
        return PredefinedFunctions.class;
    }

    public static class PredefinedFunctionsConfigModel extends FeatureConfigModel {
    }
}
