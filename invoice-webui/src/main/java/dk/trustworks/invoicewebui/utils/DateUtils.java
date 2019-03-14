package dk.trustworks.invoicewebui.utils;

import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.YearMonth;
import java.time.ZoneId;
import java.time.format.TextStyle;
import java.time.temporal.ChronoUnit;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class DateUtils {

    public static LocalDate convertJodaToJavaDate(org.joda.time.LocalDate jodaDate) {
        return LocalDate.of(jodaDate.getYear(), jodaDate.getMonthOfYear(), jodaDate.getDayOfMonth());
    }

    public static Date convertLocalDateToDate(LocalDate localDate) {
        return Date.from(localDate.atStartOfDay(ZoneId.systemDefault()).toInstant());
    }

    public static LocalDate lastDayOfMonth(LocalDate localDate) {
        return localDate.withDayOfMonth(localDate.lengthOfMonth());
    }

    public static LocalDate convertDateToLocalDate(Timestamp date) {
        return date.toLocalDateTime().toLocalDate();//.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
    }

    public static LocalDate getFirstDayOfMonth(LocalDate localDate) {
        YearMonth month = YearMonth.from(localDate);
        return month.atDay(1);
    }

    public static LocalDate getLastDayOfMonth(LocalDate localDate) {
        YearMonth month = YearMonth.from(localDate);
        return month.atEndOfMonth();
    }

    public static String[] getMonthNames(LocalDate localDateStart, LocalDate localDateEnd) {
        int monthPeriod = (int) ChronoUnit.MONTHS.between(localDateStart, localDateEnd)+1;
        String[] monthNames = new String[monthPeriod];
        for (int i = 0; i < monthNames.length; i++) {
            monthNames[i] = localDateStart.plusMonths(i).getMonth().getDisplayName(TextStyle.SHORT, Locale.ENGLISH);
        }
        return monthNames;
    }

    public static int countWeekDays(LocalDate startDate, LocalDate endDate) {
        Calendar startCal = Calendar.getInstance();
        startCal.setTime(convertLocalDateToDate(startDate));

        Calendar endCal = Calendar.getInstance();
        endCal.setTime(convertLocalDateToDate(endDate));

        int workDays = 0;

        //Return 0 if start and end are the same
        if (startCal.getTimeInMillis() == endCal.getTimeInMillis()) {
            return 0;
        }

        if (startCal.getTimeInMillis() > endCal.getTimeInMillis()) {
            startCal.setTime(convertLocalDateToDate(endDate));
            endCal.setTime(convertLocalDateToDate(startDate));
        }

        do {
            //excluding start date
            startCal.add(Calendar.DAY_OF_MONTH, 1);
            if (startCal.get(Calendar.DAY_OF_WEEK) != Calendar.SATURDAY && startCal.get(Calendar.DAY_OF_WEEK) != Calendar.SUNDAY) {
                ++workDays;
            }
        } while (startCal.getTimeInMillis() < endCal.getTimeInMillis()); //excluding end date

        return workDays;
    }
}
