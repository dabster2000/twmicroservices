package dk.trustworks.invoicewebui.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class BubbleMember {

    @JsonProperty("uuid")
    public String uuid;
    @JsonProperty("useruuid")
    public String useruuid;

}