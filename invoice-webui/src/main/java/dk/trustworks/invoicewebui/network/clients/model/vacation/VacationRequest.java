package dk.trustworks.invoicewebui.network.clients.model.vacation;

import com.fasterxml.jackson.annotation.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
        "user",
        "startDate",
        "vacationDates"
})
public class VacationRequest {

    @JsonProperty("user")
    private String user;
    @JsonProperty("startDate")
    private String startDate;
    @JsonProperty("vacationDates")
    private List<VacationDate> vacationDates = new ArrayList<>();
    @JsonIgnore
    private Map<String, Object> additionalProperties = new HashMap<>();

    /**
     * No args constructor for use in serialization
     *
     */
    public VacationRequest() {
    }

    /**
     *
     * @param vacationDates
     * @param user
     * @param startDate
     */
    public VacationRequest(String user, String startDate, List<VacationDate> vacationDates) {
        super();
        this.user = user;
        this.startDate = startDate;
        this.vacationDates = vacationDates;
    }

    @JsonProperty("user")
    public String getUser() {
        return user;
    }

    @JsonProperty("user")
    public void setUser(String user) {
        this.user = user;
    }

    public VacationRequest withUser(String user) {
        this.user = user;
        return this;
    }

    @JsonProperty("startDate")
    public String getStartDate() {
        return startDate;
    }

    @JsonProperty("startDate")
    public void setStartDate(String startDate) {
        this.startDate = startDate;
    }

    public VacationRequest withStartDate(String startDate) {
        this.startDate = startDate;
        return this;
    }

    @JsonProperty("vacationDates")
    public List<VacationDate> getVacationDates() {
        return vacationDates;
    }

    @JsonProperty("vacationDates")
    public void setVacationDates(List<VacationDate> vacationDates) {
        this.vacationDates = vacationDates;
    }

    public VacationRequest withVacationDates(List<VacationDate> vacationDates) {
        this.vacationDates = vacationDates;
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

    public VacationRequest withAdditionalProperty(String name, Object value) {
        this.additionalProperties.put(name, value);
        return this;
    }

}