package dk.trustworks.invoicewebui.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import dk.trustworks.invoicewebui.services.UserService;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.util.UUID;

@Data
@NoArgsConstructor
public class BubbleMember {

    private String uuid;

    private String useruuid;

    @JsonIgnore
    private Bubble bubble;

}