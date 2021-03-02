package ar.ne.rcs.shared.models.common;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class VersionInfo {
    Integer remoteVersion;
    String url;
}
