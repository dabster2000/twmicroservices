package dk.trustworks.invoicewebui.network.clients.model.vacation;

import java.util.HashMap;
import java.util.Map;
import com.fasterxml.jackson.annotation.JsonAnyGetter;
import com.fasterxml.jackson.annotation.JsonAnySetter;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
        "hours",
        "date"
})
public class VacationDate {

    @JsonProperty("hours")
    private double hours;
    @JsonProperty("date")
    private String date;
    @JsonIgnore
    private Map<String, Object> additionalProperties = new HashMap<String, Object>();

    /**
     * No args constructor for use in serialization
     *
     */
    public VacationDate() {
    }

    /**
     *
     * @param date
     * @param hours
     */
    public VacationDate(double hours, String date) {
        super();
        this.hours = hours;
        this.date = date;
    }

    @JsonProperty("hours")
    public double getHours() {
        return hours;
    }

    @JsonProperty("hours")
    public void setHours(double hours) {
        this.hours = hours;
    }

    public VacationDate withHours(double hours) {
        this.hours = hours;
        return this;
    }

    @JsonProperty("date")
    public String getDate() {
        return date;
    }

    @JsonProperty("date")
    public void setDate(String date) {
        this.date = date;
    }

    public VacationDate withDate(String date) {
        this.date = date;
        return this;
    }

    @JsonAnyGetter
    public Map<String, Object> getAdditionalProperties() {
        return this.additionalProperties;
    }

    @JsonAnySetter
    public void setAdditionalProperty(String name, Object value) {
        this.additionalProperties.put(name, value);
    }

    public VacationDate withAdditionalProperty(String name, Object value) {
        this.additionalProperties.put(name, value);
        return this;
    }

}