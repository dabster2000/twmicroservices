package dk.trustworks.invoicewebui.model.dto;

import dk.trustworks.invoicewebui.model.Role;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
public class LoginToken {
    private String token;
    private String useruuid;
    private boolean success = false;
    private String failureReason;
    private List<Role> roles;
}
