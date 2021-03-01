package dk.trustworks.invoicewebui.model.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateSerializer;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class CompanyAggregateData {

    @JsonProperty("month")
    @JsonSerialize(using = LocalDateSerializer.class)
    @JsonDeserialize(using = LocalDateDeserializer.class)
    public LocalDate month;
    @JsonProperty("registeredHours")
    public int registeredHours;
    @JsonProperty("budgetAmount")
    public int budgetAmount;
    @JsonProperty("budgetHours")
    public int budgetHours;
    @JsonProperty("registeredAmount")
    public int registeredAmount;
    @JsonProperty("invoicedAmount")
    public int invoicedAmount;
    @JsonProperty("consultantSalaries")
    public int consultantSalaries;
    @JsonProperty("staffSalaries")
    public int staffSalaries;
    @JsonProperty("employeeExpenses")
    public int employeeExpenses;
    @JsonProperty("officeExpenses")
    public int officeExpenses;
    @JsonProperty("salesExpenses")
    public int salesExpenses;
    @JsonProperty("productionExpenses")
    public int productionExpenses;
    @JsonProperty("administrationExpenses")
    public int administrationExpenses;
    @JsonProperty("netAvailableHours")
    private double netAvailableHours; // done
    @JsonProperty("grossAvailableHours")
    private double grossAvailableHours; // done
    @JsonProperty("netAvailability")
    private double netAvailability; // done
    @JsonProperty("grossAvailability")
    private double grossAvailability; // done
    @JsonProperty("budgetAvailability")
    private double budgetAvailability; // done
    @JsonProperty("numOfEmployees")
    private int numOfEmployees; // done
    @JsonProperty("numOfConsultants")
    private int numOfConsultants; // done

    public void addData(int registeredHours, int registeredAmount, int invoicedAmount) {
        this.registeredHours += registeredHours;
        this.registeredAmount += registeredAmount;
        this.invoicedAmount += invoicedAmount;
    }

    public int calcExpensesSum() {
        return consultantSalaries+staffSalaries+employeeExpenses+officeExpenses+salesExpenses+productionExpenses+administrationExpenses;
    }
}
