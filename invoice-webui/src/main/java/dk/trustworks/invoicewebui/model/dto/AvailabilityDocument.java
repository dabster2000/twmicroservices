package dk.trustworks.invoicewebui.model.dto;

import dk.trustworks.invoicewebui.model.User;
import dk.trustworks.invoicewebui.model.enums.ConsultantType;
import dk.trustworks.invoicewebui.model.enums.StatusType;
import dk.trustworks.invoicewebui.utils.DateUtils;
import lombok.Getter;
import lombok.ToString;

import java.time.LocalDate;

@Getter
@ToString
public class AvailabilityDocument {
    private final LocalDate month;
    private final User user;
    private final double availableHours;
    private final double vacation;
    private final double sickdays;
    private final double weeks;
    private final double weekdaysInPeriod;
    private final ConsultantType consultantType;
    private final StatusType statusType;

    public AvailabilityDocument(User user, LocalDate month, double workWeek, double vacation, double sickdays, ConsultantType consultantType, StatusType statusType) {
        this.consultantType = consultantType;
        this.statusType = statusType;
        if(user.getUsername().equals("hans.lassen"))
        System.out.println("user = [" + user + "], month = [" + month + "], workWeek = [" + workWeek + "], vacation = [" + vacation + "], sickdays = [" + sickdays + "]");
        this.user = user;
        this.vacation = vacation;
        this.month = month;
        weekdaysInPeriod = DateUtils.getWeekdaysInPeriod(month, month.plusMonths(1).minusDays(1));
        this.sickdays = sickdays;
        weeks = weekdaysInPeriod / 5.0;
        double result = ((workWeek - 2) * weeks) - vacation;
        availableHours = (result<0.0)?0.0:result;
        if(user.getUsername().equals("hans.lassen"))
            System.out.println(toString());
    }
}
