package ar.ne.rcs.shared.models.connection;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class LoginResponse {
    String token;
    LoginResult result;
}
