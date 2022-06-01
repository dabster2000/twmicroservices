package dk.trustworks.invoicewebui.model;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class PerformanceResult {

    private String uuid;
    private String pk_uuid;
    private int result;

}
