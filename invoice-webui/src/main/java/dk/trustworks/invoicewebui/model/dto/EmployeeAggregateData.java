package dk.trustworks.invoicewebui.model.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateSerializer;
import dk.trustworks.invoicewebui.TrustworksConfiguration;
import dk.trustworks.invoicewebui.model.Team;
import dk.trustworks.invoicewebui.model.enums.ConsultantType;
import dk.trustworks.invoicewebui.model.enums.StatusType;
import dk.trustworks.invoicewebui.utils.DateUtils;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Data
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class EmployeeAggregateData {

    @NonNull
    @JsonSerialize(using = LocalDateSerializer.class)
    @JsonDeserialize(using = LocalDateDeserializer.class)
    private LocalDate month;
    @NonNull
    private String useruuid;
    @JsonProperty("registeredHours")
    public double registeredHours;
    @JsonProperty("helpedColleagueHours")
    public double helpedColleagueHours;
    @JsonProperty("gotHelpByColleagueHours")
    public double gotHelpByColleagueHours;
    @JsonProperty("registeredAmount")
    public double registeredAmount;
    @JsonProperty("contractUtilization")
    public double contractUtilization;
    @JsonProperty("actualUtilization")
    public double actualUtilization;
    @JsonProperty("netAvailableHours")
    public double netAvailableHours;
    @JsonProperty("grossAvailableHours")
    public double grossAvailableHours;
    @JsonProperty("availableHours")
    public double availableHours;
    @JsonProperty("budgetAmount")
    public double budgetAmount;
    @JsonProperty("budgetHours")
    public double budgetHours;
    @JsonProperty("budgetHoursWithNoAvailabilityAdjustment")
    private double budgetHoursWithNoAvailabilityAdjustment; // done
    @JsonProperty("budgetDocuments")
    private List<BudgetDocument> budgetDocuments = new ArrayList<>();
    @JsonProperty("salary")
    public double salary;
    @JsonProperty("sharedExpenses")
    public double sharedExpenses;
    @JsonProperty("vacation")
    public double vacation;
    @JsonProperty("sickdays")
    public double sickdays;
    @JsonProperty("maternityLeave")
    public double maternityLeave;
    @JsonProperty("weeks")
    public double weeks;
    @JsonProperty("weekdaysInPeriod")
    public double weekdaysInPeriod;
    @JsonProperty("consultantType")
    public ConsultantType consultantType;
    @JsonProperty("statusType")
    public StatusType statusType;
    @JsonProperty("teamMemberOf")
    private List<Team> teamMemberOf = new ArrayList<>();
    @JsonProperty("teamLeaderOf")
    private List<Team> teamLeaderOf = new ArrayList<>();
    @JsonProperty("teamSponsorOf")
    private List<Team> teamSponsorOf = new ArrayList<>();
    @JsonProperty("teamGuestOf")
    private List<Team> teamGuestOf = new ArrayList<>();

}
