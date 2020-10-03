package dk.trustworks.invoicewebui.model.dto;

import com.fasterxml.jackson.annotation.*;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateSerializer;
import dk.trustworks.invoicewebui.TrustworksConfiguration;
import dk.trustworks.invoicewebui.model.User;
import dk.trustworks.invoicewebui.model.enums.ConsultantType;
import dk.trustworks.invoicewebui.model.enums.StatusType;
import dk.trustworks.invoicewebui.utils.DateUtils;
import org.apache.commons.lang3.builder.ToStringBuilder;

import java.time.DayOfWeek;
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
    private Double availableHours;
    @JsonProperty("vacation")
    private Double vacation;
    @JsonProperty("sickdays")
    private Double sickdays;
    @JsonProperty("maternityLeave")
    private Double maternityLeave;
    @JsonProperty("weeks")
    private Double weeks;
    @JsonProperty("weekdaysInPeriod")
    private Double weekdaysInPeriod;
    @JsonProperty("consultantType")
    private ConsultantType consultantType;
    @JsonProperty("statusType")
    private StatusType statusType;
    @JsonProperty("grossAvailableHours")
    private Double grossAvailableHours;
    @JsonProperty("netAvailableHours")
    private Double netAvailableHours;
    @JsonProperty("grossVacation")
    private Double grossVacation;
    @JsonProperty("netVacation")
    private Double netVacation;
    @JsonProperty("grossSickdays")
    private Double grossSickdays;
    @JsonProperty("netSickdays")
    private Double netSickdays;
    @JsonProperty("netMaternityLeave")
    private Double netMaternityLeave;
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
    public Double getAvailableHours() {
        return availableHours;
    }

    @JsonProperty("availableHours")
    public void setAvailableHours(Double availableHours) {
        this.availableHours = availableHours;
    }

    @JsonProperty("vacation")
    public Double getVacation() {
        return vacation;
    }

    @JsonProperty("vacation")
    public void setVacation(Double vacation) {
        this.vacation = vacation;
    }

    @JsonProperty("sickdays")
    public Double getSickdays() {
        return sickdays;
    }

    @JsonProperty("sickdays")
    public void setSickdays(Double sickdays) {
        this.sickdays = sickdays;
    }

    @JsonProperty("maternityLeave")
    public Double getMaternityLeave() {
        return maternityLeave;
    }

    @JsonProperty("maternityLeave")
    public void setMaternityLeave(Double maternityLeave) {
        this.maternityLeave = maternityLeave;
    }

    @JsonProperty("weeks")
    public Double getWeeks() {
        return weeks;
    }

    @JsonProperty("weeks")
    public void setWeeks(Double weeks) {
        this.weeks = weeks;
    }

    @JsonProperty("weekdaysInPeriod")
    public Double getWeekdaysInPeriod() {
        return weekdaysInPeriod;
    }

    @JsonProperty("weekdaysInPeriod")
    public void setWeekdaysInPeriod(Double weekdaysInPeriod) {
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
    public Double getGrossAvailableHours() {
        return grossAvailableHours;
    }

    @JsonProperty("grossAvailableHours")
    public void setGrossAvailableHours(Double grossAvailableHours) {
        this.grossAvailableHours = grossAvailableHours;
    }

    @JsonProperty("netAvailableHours")
    public Double getNetAvailableHours() {
        return netAvailableHours;
    }

    @JsonProperty("netAvailableHours")
    public void setNetAvailableHours(Double netAvailableHours) {
        this.netAvailableHours = netAvailableHours;
    }

    @JsonProperty("grossVacation")
    public Double getGrossVacation() {
        return grossVacation;
    }

    @JsonProperty("grossVacation")
    public void setGrossVacation(Double grossVacation) {
        this.grossVacation = grossVacation;
    }

    @JsonProperty("netVacation")
    public Double getNetVacation() {
        return netVacation;
    }

    @JsonProperty("netVacation")
    public void setNetVacation(Double netVacation) {
        this.netVacation = netVacation;
    }

    @JsonProperty("grossSickdays")
    public Double getGrossSickdays() {
        return grossSickdays;
    }

    @JsonProperty("grossSickdays")
    public void setGrossSickdays(Double grossSickdays) {
        this.grossSickdays = grossSickdays;
    }

    @JsonProperty("netSickdays")
    public Double getNetSickdays() {
        return netSickdays;
    }

    @JsonProperty("netSickdays")
    public void setNetSickdays(Double netSickdays) {
        this.netSickdays = netSickdays;
    }

    @JsonProperty("netMaternityLeave")
    public Double getNetMaternityLeave() {
        return netMaternityLeave;
    }

    @JsonProperty("netMaternityLeave")
    public void setNetMaternityLeave(Double netMaternityLeave) {
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

/*
@JsonIgnoreProperties(ignoreUnknown = true)
public class AvailabilityDocument {
    @JsonDeserialize(using = LocalDateDeserializer.class)
    @JsonSerialize(using = LocalDateSerializer.class)
    private LocalDate month;
    private User user;
    private double availableHours;
    private double vacation;
    private double sickdays;
    private double maternityLeave;
    private double weeks;
    private double weekdaysInPeriod;
    private ConsultantType consultantType;
    private StatusType statusType;

    public AvailabilityDocument() {
    }

    public AvailabilityDocument(User user, LocalDate month, double workWeek, double vacation, double sickdays, double maternityLeave, ConsultantType consultantType, StatusType statusType) {
        this.maternityLeave = maternityLeave;
        this.consultantType = consultantType;
        this.statusType = statusType;
        this.user = user;
        this.vacation = vacation;
        this.month = month;
        weekdaysInPeriod = DateUtils.getWeekdaysInPeriod(month, month.plusMonths(1));
        this.sickdays = sickdays;
        weeks = weekdaysInPeriod / 5.0;
        availableHours = workWeek;
    }

    /**
     * Hvilken måned dette dokument vedrører (f.eks. 2019-12-1, hvor dagen smides væk)
     * @return Den relevante måned

    public LocalDate getMonth() {
        return month;
    }

    /**
     * Den aktuelle bruger
     * @return Den aktuelle bruger

    public User getUser() {
        return user;
    }

    /**
     * Total availability i henhold til ansættelseskontrakt, f.eks. 37 timer.
     * @return Total availability i henhold til ansættelseskontrakt, f.eks. 37 timer

    public double getGrossAvailableHours() {
        return Math.max(availableHours * weeks, 0.0);
    }

    /**
     * Det antal timer, som konsulenten er tilgængelig, minus de to timer der bruges om fredagen samt eventuelt ferie og sygdom.
     * @return availability uden ferie, sygdom og fredage

    public double getNetAvailableHours() {
        return Math.max((availableHours * weeks) - adjustForOffHours() - getNetVacation() - getNetSickdays() - getNetMaternityLeave(), 0.0); // F.eks. 2019-12-01: ((37 - 2) * 3,6) - (7,4 * 2 - 0.4) - (0 * 1)) = 111,2
    }

    public double getGrossVacation() {
        return vacation;
    }

    public double getNetVacation() {
        return vacation;
    }

    public double getGrossSickdays() {
        return sickdays;
    }

    public double getNetSickdays() {
        return sickdays;
    }

    public double getNetMaternityLeave() {
        return maternityLeave;
    }

    private double adjustForOffHours() {
        int numberOfFridaysInPeriod = DateUtils.countWeekdayOccurances(DayOfWeek.FRIDAY, getMonth(), getMonth().plusMonths(1));
        int numberOfFridayHolidays = DateUtils.getVacationDayArray(getMonth().getYear()).stream()
                .filter(localDate -> localDate.getMonthValue() == getMonth().getMonthValue())
                .mapToInt(value -> (value.getDayOfWeek().getValue() != DayOfWeek.FRIDAY.getValue()) ? 0 : 1).sum();
        return (numberOfFridaysInPeriod - numberOfFridayHolidays) * TrustworksConfiguration.getWeekOffHours();
    }

    public double getWeeks() {
        return weeks;
    }

    public double getWeekdaysInPeriod() {
        return weekdaysInPeriod;
    }

    public ConsultantType getConsultantType() {
        return consultantType;
    }

    public StatusType getStatusType() {
        return statusType;
    }

    @Override
    public String toString() {
        return "AvailabilityDocument{" +
                "month=" + month +
                ", user=" + user.getUsername() +
                ", availableHours=" + availableHours +
                ", vacation=" + vacation +
                ", sickdays=" + sickdays +
                ", maternityLeave=" + maternityLeave +
                ", weeks=" + weeks +
                ", weekdaysInPeriod=" + weekdaysInPeriod +
                ", consultantType=" + consultantType +
                ", statusType=" + statusType +
                '}';
    }
}
*/