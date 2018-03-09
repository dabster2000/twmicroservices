package dk.trustworks.invoicewebui.model.rest.cryptocompare;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

import java.util.ArrayList;
import java.util.List;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
        "Response",
        "Type",
        "Aggregated",
        "Data",
        "TimeTo",
        "TimeFrom",
        "FirstValueInArray",
        "ConversionType"
})
public class CryptoCompareRoot {

    @JsonProperty("Response")
    private String response;
    @JsonProperty("Type")
    private int type;
    @JsonProperty("Aggregated")
    private boolean aggregated;
    @JsonProperty("Data")
    private List<Datum> data = new ArrayList<>();
    @JsonProperty("TimeTo")
    private int timeTo;
    @JsonProperty("TimeFrom")
    private int timeFrom;
    @JsonProperty("FirstValueInArray")
    private boolean firstValueInArray;
    @JsonProperty("ConversionType")
    private ConversionType conversionType;

    @JsonProperty("Response")
    public String getResponse() {
        return response;
    }

    @JsonProperty("Response")
    public void setResponse(String response) {
        this.response = response;
    }

    @JsonProperty("Type")
    public int getType() {
        return type;
    }

    @JsonProperty("Type")
    public void setType(int type) {
        this.type = type;
    }

    @JsonProperty("Aggregated")
    public boolean isAggregated() {
        return aggregated;
    }

    @JsonProperty("Aggregated")
    public void setAggregated(boolean aggregated) {
        this.aggregated = aggregated;
    }

    @JsonProperty("Data")
    public List<Datum> getData() {
        return data;
    }

    @JsonProperty("Data")
    public void setData(List<Datum> data) {
        this.data = data;
    }

    @JsonProperty("TimeTo")
    public int getTimeTo() {
        return timeTo;
    }

    @JsonProperty("TimeTo")
    public void setTimeTo(int timeTo) {
        this.timeTo = timeTo;
    }

    @JsonProperty("TimeFrom")
    public int getTimeFrom() {
        return timeFrom;
    }

    @JsonProperty("TimeFrom")
    public void setTimeFrom(int timeFrom) {
        this.timeFrom = timeFrom;
    }

    @JsonProperty("FirstValueInArray")
    public boolean isFirstValueInArray() {
        return firstValueInArray;
    }

    @JsonProperty("FirstValueInArray")
    public void setFirstValueInArray(boolean firstValueInArray) {
        this.firstValueInArray = firstValueInArray;
    }

    @JsonProperty("ConversionType")
    public ConversionType getConversionType() {
        return conversionType;
    }

    @JsonProperty("ConversionType")
    public void setConversionType(ConversionType conversionType) {
        this.conversionType = conversionType;
    }

}

