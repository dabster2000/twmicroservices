package dk.trustworks.invoicewebui.model.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateSerializer;
import dk.trustworks.invoicewebui.model.enums.ConsultantType;
import dk.trustworks.invoicewebui.model.enums.StatusType;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

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
    //@JsonProperty("budgetDocuments")
    //private List<BudgetDocument> budgetDocuments = new ArrayList<>();

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
    private String teamMemberOf;
    @JsonProperty("teamLeaderOf")
    private String teamLeaderOf;
    @JsonProperty("teamSponsorOf")
    private String teamSponsorOf;
    @JsonProperty("teamGuestOf")
    private String teamGuestOf;

    public List<String> getTeamMemberOf() {
        if(teamMemberOf==null || teamMemberOf.length()<2) return new ArrayList<>();
        return Stream.of(teamMemberOf.split(",", -1)).collect(Collectors.toList());
    }

    public List<String> getTeamLeaderOf() {
        if(teamMemberOf==null || teamLeaderOf.length()<2) return new ArrayList<>();
        return Stream.of(teamLeaderOf.split(",", -1)).collect(Collectors.toList());
    }

    public List<String> getTeamSponsorOf() {
        if(teamMemberOf==null || teamSponsorOf.length()<2) return new ArrayList<>();
        return Stream.of(teamSponsorOf.split(",", -1)).collect(Collectors.toList());
    }

    public List<String> getTeamGuestOf() {
        if(teamMemberOf==null || teamGuestOf.length()<2) return new ArrayList<>();
        return Stream.of(teamGuestOf.split(",", -1)).collect(Collectors.toList());
    }
}
