package dk.trustworks.invoicewebui.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class PerformanceKey {

    @JsonProperty("uuid")
    private String uuid;
    @JsonProperty("pg_uuid")
    private String pgUuid;
    @JsonProperty("name")
    private String name;
    @JsonProperty("description")
    private String description;

}
