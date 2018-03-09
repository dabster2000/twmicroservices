package dk.trustworks.invoicewebui.model.rest.cryptocompare;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
        "type",
        "conversionSymbol"
})
public class ConversionType {

    @JsonProperty("type")
    private String type;
    @JsonProperty("conversionSymbol")
    private String conversionSymbol;

    @JsonProperty("type")
    public String getType() {
        return type;
    }

    @JsonProperty("type")
    public void setType(String type) {
        this.type = type;
    }

    @JsonProperty("conversionSymbol")
    public String getConversionSymbol() {
        return conversionSymbol;
    }

    @JsonProperty("conversionSymbol")
    public void setConversionSymbol(String conversionSymbol) {
        this.conversionSymbol = conversionSymbol;
    }
}
