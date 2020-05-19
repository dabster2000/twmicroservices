package dk.trustworks.invoicewebui.model.dto;

import dk.trustworks.invoicewebui.TrustworksConfiguration;
import dk.trustworks.invoicewebui.model.User;
import dk.trustworks.invoicewebui.model.enums.ConsultantType;
import dk.trustworks.invoicewebui.model.enums.StatusType;
import dk.trustworks.invoicewebui.utils.DateUtils;

import java.time.DayOfWeek;
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
        availableHours = workWeek;
    }

    /**
     * Hvilken måned dette dokument vedrører (f.eks. 2019-12-1, hvor dagen smides væk)
     * @return Den relevante måned
     */
    public LocalDate getMonth() {
        return month;
    }

    /**
     * Den aktuelle bruger
     * @return Den aktuelle bruger
     */
    public User getUser() {
        return user;
    }

    /**
     * Total availability i henhold til ansættelseskontrakt, f.eks. 37 timer.
     * @return Total availability i henhold til ansættelseskontrakt, f.eks. 37 timer
     */
    public double getGrossAvailableHours() {
        return Math.max(availableHours * weeks, 0.0);
    }

    /**
     * Det antal timer, som konsulenten er tilgængelig, minus de to timer der bruges om fredagen samt eventuelt ferie og sygdom.
     * @return availability uden ferie, sygdom og fredage
     */
    public double getNetAvailableHours() {
        return Math.max((availableHours * weeks) - adjustForOffHours() - getNetVacation() - getNetSickdays(), 0.0); // F.eks. 2019-12-01: ((37 - 2) * 3,6) - (7,4 * 2 - 0.4) - (0 * 1)) = 111,2
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

    private double adjustForOffHours() {
        if(user.getUsername().equals("hans.lassen") && month.isEqual(LocalDate.of(2019, 12, 1)))
            System.out.println("month = " + month);
        int numberOfFridaysInPeriod = DateUtils.countWeekdayOccurances(DayOfWeek.FRIDAY, getMonth(), getMonth().plusMonths(1));
        if(user.getUsername().equals("hans.lassen") && getMonth().isEqual(LocalDate.of(2019, 12, 1)))
            System.out.println("numberOfFridaysInPeriod = " + numberOfFridaysInPeriod);
        for (LocalDate localDate : DateUtils.getVacationDayArray(getMonth().getYear())) {
            if(user.getUsername().equals("hans.lassen") && getMonth().isEqual(LocalDate.of(2019, 12, 1)))
                System.out.println("localDate = " + localDate);
        }

        int numberOfFridayHolidays = DateUtils.getVacationDayArray(getMonth().getYear()).stream()
                .filter(localDate -> localDate.getMonthValue() == getMonth().getMonthValue())
                .mapToInt(value -> (value.getDayOfWeek().getValue() != DayOfWeek.FRIDAY.getValue()) ? 0 : 1).sum();
        if(user.getUsername().equals("hans.lassen") && getMonth().isEqual(LocalDate.of(2019, 12, 1)))
            System.out.println("numberOfFridayHolidays = " + numberOfFridayHolidays);
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
