package ar.ne.rcs.android.features;

import ar.ne.rcs.android.RCSAndroidManager;

import java.util.HashMap;

public abstract class Feature<T extends FeatureConfigModel> {
    private static final HashMap<Class<? extends Feature<? extends FeatureConfigModel>>, Feature<? extends FeatureConfigModel>> features = new HashMap<>();
    protected final RCSAndroidManager manager;
    protected final T configModel;

    protected Feature(RCSAndroidManager manager, T configModel) {
        this.manager = manager;
        this.configModel = configModel;
        register();
    }

    @SuppressWarnings("unchecked")
    public static <T extends Feature<? extends FeatureConfigModel>> T get(Class<? extends Feature<? extends FeatureConfigModel>> feature) {
        return (T) features.get(feature);
    }

    @SuppressWarnings("unchecked")
    public static Feature<? extends FeatureConfigModel>[] getEnabledFeature() {
        return features.values().toArray(new Feature[0]);
    }

    abstract Class<? extends Feature<? extends FeatureConfigModel>> getFeatureType();

    private void register() {
        features.put(getFeatureType(), this);
    }

    public T getConfig() {
        return configModel;
    }

}
