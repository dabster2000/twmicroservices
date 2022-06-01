package dk.trustworks.invoicewebui.network.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateSerializer;
import lombok.Data;

import java.time.LocalDate;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
        "clientuuid",
        "month",
        "marginResult"
})
public class ClientMarginResult {
    @JsonProperty("clientuuid")
    public String clientuuid;
    @JsonProperty("month")
    @JsonDeserialize(using = LocalDateDeserializer.class)
    @JsonSerialize(using = LocalDateSerializer.class)
    public LocalDate month;
    @JsonProperty("marginResult")
    public MarginResult marginResult;

    /**
     * No args constructor for use in serialization
     *
     */
    public ClientMarginResult() {
    }

    /**
     *
     * @param clientuuid
     * @param month
     * @param marginResult
     */
    public ClientMarginResult(String clientuuid, LocalDate month, MarginResult marginResult) {
        super();
        this.clientuuid = clientuuid;
        this.month = month;
        this.marginResult = marginResult;
    }

    public String getClientuuid() {
        return clientuuid;
    }

    public void setClientuuid(String clientuuid) {
        this.clientuuid = clientuuid;
    }

    public LocalDate getMonth() {
        return month;
    }

    public void setMonth(LocalDate month) {
        this.month = month;
    }

    public MarginResult getMarginResult() {
        return marginResult;
    }

    public void setMarginResult(MarginResult marginResult) {
        this.marginResult = marginResult;
    }
}

