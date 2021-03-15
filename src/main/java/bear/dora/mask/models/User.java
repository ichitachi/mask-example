package bear.dora.mask.models;

import bear.dora.mask.annotations.Mask;
import bear.dora.mask.annotations.PII;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

@Getter
@Setter
@Builder
@Mask
public class User implements Serializable {
    private String name;
    private String username;
    @PII
    private String password;
    @PII(keepLastDigits = 3, pattern = "$")
    private String code;
}
