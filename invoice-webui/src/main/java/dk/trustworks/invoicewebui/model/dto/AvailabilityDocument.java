package dk.trustworks.invoicewebui.model.dto;

import com.fasterxml.jackson.annotation.*;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateSerializer;
import dk.trustworks.invoicewebui.model.User;
import dk.trustworks.invoicewebui.model.enums.ConsultantType;
import dk.trustworks.invoicewebui.model.enums.StatusType;
import dk.trustworks.invoicewebui.utils.DateUtils;
import org.apache.commons.lang3.builder.ToStringBuilder;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class AvailabilityDocument {

    @JsonProperty("month")
    @JsonDeserialize(using = LocalDateDeserializer.class)
    @JsonSerialize(using = LocalDateSerializer.class)
    private LocalDate month;
    @JsonProperty("user")
    private User user;
    @JsonProperty("availableHours")
    private double availableHours;
    @JsonProperty("vacation")
    private double vacation;
    @JsonProperty("sickdays")
    private double sickdays;
    @JsonProperty("maternityLeave")
    private double maternityLeave;
    @JsonProperty("weeks")
    private double weeks;
    @JsonProperty("weekdaysInPeriod")
    private double weekdaysInPeriod;
    @JsonProperty("consultantType")
    private ConsultantType consultantType;
    @JsonProperty("statusType")
    private StatusType statusType;
    @JsonProperty("grossAvailableHours")
    private double grossAvailableHours;
    @JsonProperty("netAvailableHours")
    private double netAvailableHours;
    @JsonProperty("grossVacation")
    private double grossVacation;
    @JsonProperty("netVacation")
    private double netVacation;
    @JsonProperty("grossSickdays")
    private double grossSickdays;
    @JsonProperty("netSickdays")
    private double netSickdays;
    @JsonProperty("netMaternityLeave")
    private double netMaternityLeave;
    @JsonIgnore
    private Map<String, Object> additionalProperties = new HashMap<String, Object>();

    public AvailabilityDocument() {
    }

    public AvailabilityDocument(User user, LocalDate month, double workWeek, double vacation, double sickdays, double maternityLeave, ConsultantType consultantType, StatusType statusType) {
        this.maternityLeave = maternityLeave;
        this.consultantType = consultantType;
        this.statusType = statusType;
        this.user = user;
        this.vacation = vacation;
        this.month = month;
        weekdaysInPeriod = (double)DateUtils.getWeekdaysInPeriod(month, month.plusMonths(1));
        this.sickdays = sickdays;
        weeks = weekdaysInPeriod / 5.0;
        availableHours = workWeek;
    }

    @JsonProperty("month")

    @JsonDeserialize(using = LocalDateDeserializer.class)
    @JsonSerialize(using = LocalDateSerializer.class)
    public LocalDate getMonth() {
        return month;
    }

    @JsonProperty("month")
    @JsonDeserialize(using = LocalDateDeserializer.class)
    @JsonSerialize(using = LocalDateSerializer.class)
    public void setMonth(LocalDate month) {
        this.month = month;
    }

    @JsonProperty("user")
    public User getUser() {
        return user;
    }

    @JsonProperty("user")
    public void setUser(User user) {
        this.user = user;
    }

    @JsonProperty("availableHours")
    public double getAvailableHours() {
        return availableHours;
    }

    @JsonProperty("availableHours")
    public void setAvailableHours(double availableHours) {
        this.availableHours = availableHours;
    }

    @JsonProperty("vacation")
    public double getVacation() {
        return vacation;
    }

    @JsonProperty("vacation")
    public void setVacation(double vacation) {
        this.vacation = vacation;
    }

    @JsonProperty("sickdays")
    public double getSickdays() {
        return sickdays;
    }

    @JsonProperty("sickdays")
    public void setSickdays(double sickdays) {
        this.sickdays = sickdays;
    }

    @JsonProperty("maternityLeave")
    public double getMaternityLeave() {
        return maternityLeave;
    }

    @JsonProperty("maternityLeave")
    public void setMaternityLeave(double maternityLeave) {
        this.maternityLeave = maternityLeave;
    }

    @JsonProperty("weeks")
    public double getWeeks() {
        return weeks;
    }

    @JsonProperty("weeks")
    public void setWeeks(double weeks) {
        this.weeks = weeks;
    }

    @JsonProperty("weekdaysInPeriod")
    public double getWeekdaysInPeriod() {
        return weekdaysInPeriod;
    }

    @JsonProperty("weekdaysInPeriod")
    public void setWeekdaysInPeriod(double weekdaysInPeriod) {
        this.weekdaysInPeriod = weekdaysInPeriod;
    }

    @JsonProperty("consultantType")
    public ConsultantType getConsultantType() {
        return consultantType;
    }

    @JsonProperty("consultantType")
    public void setConsultantType(ConsultantType consultantType) {
        this.consultantType = consultantType;
    }

    @JsonProperty("statusType")
    public StatusType getStatusType() {
        return statusType;
    }

    @JsonProperty("statusType")
    public void setStatusType(StatusType statusType) {
        this.statusType = statusType;
    }

    @JsonProperty("grossAvailableHours")
    public double getGrossAvailableHours() {
        return grossAvailableHours;
    }

    @JsonProperty("grossAvailableHours")
    public void setGrossAvailableHours(double grossAvailableHours) {
        this.grossAvailableHours = grossAvailableHours;
    }

    @JsonProperty("netAvailableHours")
    public double getNetAvailableHours() {
        return netAvailableHours;
    }

    @JsonProperty("netAvailableHours")
    public void setNetAvailableHours(double netAvailableHours) {
        this.netAvailableHours = netAvailableHours;
    }

    @JsonProperty("grossVacation")
    public double getGrossVacation() {
        return grossVacation;
    }

    @JsonProperty("grossVacation")
    public void setGrossVacation(double grossVacation) {
        this.grossVacation = grossVacation;
    }

    @JsonProperty("netVacation")
    public double getNetVacation() {
        return netVacation;
    }

    @JsonProperty("netVacation")
    public void setNetVacation(double netVacation) {
        this.netVacation = netVacation;
    }

    @JsonProperty("grossSickdays")
    public double getGrossSickdays() {
        return grossSickdays;
    }

    @JsonProperty("grossSickdays")
    public void setGrossSickdays(double grossSickdays) {
        this.grossSickdays = grossSickdays;
    }

    @JsonProperty("netSickdays")
    public double getNetSickdays() {
        return netSickdays;
    }

    @JsonProperty("netSickdays")
    public void setNetSickdays(double netSickdays) {
        this.netSickdays = netSickdays;
    }

    @JsonProperty("netMaternityLeave")
    public double getNetMaternityLeave() {
        return netMaternityLeave;
    }

    @JsonProperty("netMaternityLeave")
    public void setNetMaternityLeave(double netMaternityLeave) {
        this.netMaternityLeave = netMaternityLeave;
    }

    @JsonAnyGetter
    public Map<String, Object> getAdditionalProperties() {
        return this.additionalProperties;
    }

    @JsonAnySetter
    public void setAdditionalProperty(String name, Object value) {
        this.additionalProperties.put(name, value);
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this)
                .append("month", month)
                .append("user", user.getUsername())
                .append("availableHours", availableHours)
                .append("vacation", vacation).append("sickdays", sickdays).append("maternityLeave", maternityLeave).append("weeks", weeks).append("weekdaysInPeriod", weekdaysInPeriod).append("consultantType", consultantType).append("statusType", statusType).append("grossAvailableHours", grossAvailableHours).append("netAvailableHours", netAvailableHours).append("grossVacation", grossVacation).append("netVacation", netVacation).append("grossSickdays", grossSickdays).append("netSickdays", netSickdays).append("netMaternityLeave", netMaternityLeave).append("additionalProperties", additionalProperties).toString();
    }

}
