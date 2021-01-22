package ar.ne.rcs.shared.models.connection;

import ar.ne.rcs.shared.models.device.Identifier;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class Login {
    Identifier identifier;
}
