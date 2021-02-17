package ar.ne.rcs.shared.models.configs;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class RCSAndroidConfigModel {
    private CommunicationConfigModel communicationConfig;
}
