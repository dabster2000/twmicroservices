package dk.trustworks.invoicewebui.model;

import dk.trustworks.invoicewebui.model.enums.SalesStatus;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ContractSalesConsultant {

    private String uuid;

    private String contractuuid;

    private String salesconsultant;

    private SalesStatus status;

    private LocalDateTime created;

}
