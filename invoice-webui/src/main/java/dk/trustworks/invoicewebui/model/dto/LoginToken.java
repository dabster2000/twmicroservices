package dk.trustworks.invoicewebui.model.dto;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class LoginToken {
    private String token;
    private String useruuid;
    private boolean success;
    private String failureReason;
}
