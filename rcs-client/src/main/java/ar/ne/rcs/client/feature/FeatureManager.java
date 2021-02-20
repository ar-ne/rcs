package ar.ne.rcs.client.feature;

public abstract class FeatureManager {
    public static volatile FeatureManager MANAGER;

    public abstract <T extends Feature<? extends FeatureConfigModel>> T getFeature(Class<T> featureType);

    public abstract <T extends FeatureConfigModel> void enableFeature(Class<? extends Feature<? extends FeatureConfigModel>> feature, T config);
}
