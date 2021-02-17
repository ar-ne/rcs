package ar.ne.rcs.android.features;

import ar.ne.rcs.android.RCSAndroidManager;

//TODO: enable this
public class RemoteControl extends Feature<RemoteControl.RemoteControlConfigModel> {

    protected RemoteControl(RCSAndroidManager manager, RemoteControlConfigModel configModel) {
        super(manager, configModel);
    }

    @Override
    Class<? extends Feature<? extends FeatureConfigModel>> getFeatureType() {
        return this.getClass();
    }

    public static class RemoteControlConfigModel extends FeatureConfigModel {
    }
}
