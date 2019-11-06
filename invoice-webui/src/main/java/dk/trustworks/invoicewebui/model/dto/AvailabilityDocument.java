package dk.trustworks.invoicewebui.model.dto;

import dk.trustworks.invoicewebui.model.User;
import dk.trustworks.invoicewebui.model.enums.ConsultantType;
import dk.trustworks.invoicewebui.model.enums.StatusType;
import dk.trustworks.invoicewebui.utils.DateUtils;

import java.time.LocalDate;

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
        this.user = user;
        this.vacation = vacation;
        this.month = month;
        weekdaysInPeriod = DateUtils.getWeekdaysInPeriod(month, month.plusMonths(1));
        this.sickdays = sickdays;
        weeks = weekdaysInPeriod / 5.0;
        double result = ((workWeek - 2) * weeks) - vacation;
        availableHours = Math.max(result, 0.0);
    }

    public LocalDate getMonth() {
        return month;
    }

    public User getUser() {
        return user;
    }

    public double getAvailableHours() {
        return availableHours;
    }

    public double getVacation() {
        return vacation;
    }

    public double getSickdays() {
        return sickdays;
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
                ", user=" + user +
                ", availableHours=" + availableHours +
                ", vacation=" + vacation +
                ", sickdays=" + sickdays +
                ", weeks=" + weeks +
                ", weekdaysInPeriod=" + weekdaysInPeriod +
                ", consultantType=" + consultantType +
                ", statusType=" + statusType +
                '}';
    }
}
