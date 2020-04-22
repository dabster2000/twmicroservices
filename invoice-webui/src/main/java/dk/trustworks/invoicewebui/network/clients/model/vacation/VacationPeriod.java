package dk.trustworks.invoicewebui.network.clients.model.vacation;

import com.fasterxml.jackson.annotation.*;

import java.util.HashMap;
import java.util.Map;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
        "from",
        "to",
        "hoursLeft"
})
public class VacationPeriod {

    @JsonProperty("from")
    private String from;
    @JsonProperty("to")
    private String to;
    @JsonProperty("hoursUsed")
    private double hoursUsed;
    @JsonProperty("hoursLeft")
    private double hoursLeft;
    @JsonIgnore
    private Map<String, Object> additionalProperties = new HashMap<String, Object>();

    /**
     * No args constructor for use in serialization
     *
     */
    public VacationPeriod() {
    }

    /**
     *
     * @param from
     * @param to
     * @param hoursLeft
     */
    public VacationPeriod(String from, String to, double hoursLeft) {
        super();
        this.from = from;
        this.to = to;
        this.hoursLeft = hoursLeft;
    }

    @JsonProperty("from")
    public String getFrom() {
        return from;
    }

    @JsonProperty("from")
    public void setFrom(String from) {
        this.from = from;
    }

    public VacationPeriod withFrom(String from) {
        this.from = from;
        return this;
    }

    @JsonProperty("to")
    public String getTo() {
        return to;
    }

    @JsonProperty("to")
    public void setTo(String to) {
        this.to = to;
    }

    public VacationPeriod withTo(String to) {
        this.to = to;
        return this;
    }

    @JsonProperty("hoursUsed")
    public double getHoursUsed() {
        return hoursUsed;
    }

    @JsonProperty("hoursUsed")
    public void setHoursUsed(double hoursUsed) {
        this.hoursUsed = hoursUsed;
    }

    public VacationPeriod withHoursUsed(double hoursUsed) {
        this.hoursUsed = hoursUsed;
        return this;
    }

    @JsonProperty("hoursLeft")
    public double getHoursLeft() {
        return hoursLeft;
    }

    @JsonProperty("hoursLeft")
    public void setHoursLeft(double hoursLeft) {
        this.hoursLeft = hoursLeft;
    }

    public VacationPeriod withHoursLeft(double hoursLeft) {
        this.hoursLeft = hoursLeft;
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

    public VacationPeriod withAdditionalProperty(String name, Object value) {
        this.additionalProperties.put(name, value);
        return this;
    }

}