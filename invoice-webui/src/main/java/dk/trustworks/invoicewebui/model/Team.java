package dk.trustworks.invoicewebui.model;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class Team {

    private String uuid;
    private String name;
    private String shortname;
    private String logouuid;

}
