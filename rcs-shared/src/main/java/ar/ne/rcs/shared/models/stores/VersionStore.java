package ar.ne.rcs.shared.models.stores;

import ar.ne.rcs.shared.models.common.VersionInfo;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class VersionStore {
    /**
     * id as Version Group
     */
    String id;
    VersionInfo versionInfo;
}
