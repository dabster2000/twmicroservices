package dk.trustworks.invoicewebui.model.dto;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class LoginToken {
    private String token;
    private boolean success;
    private String failureReason;
}
