package ar.ne.rcs.shared.models.common;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RESTResult {
    Integer code;
    String msg;

    public RESTResult(Integer code) {
        this.code = code;
    }
}
