package dk.trustworks.invoicewebui.web.admin.model;

import dk.trustworks.invoicewebui.model.enums.TeamMemberType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TeamRolesGridItem {
    private String uuid;
    private String team;
    private LocalDate startdate;
    private LocalDate enddate;
    private TeamMemberType teammembertype;
}
