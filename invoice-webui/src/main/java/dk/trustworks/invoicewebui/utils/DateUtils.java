package dk.trustworks.invoicewebui.utils;

import lombok.experimental.UtilityClass;

import java.sql.Timestamp;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.YearMonth;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.format.TextStyle;
import java.time.temporal.ChronoUnit;
import java.util.*;

import static java.time.temporal.ChronoUnit.DAYS;

@UtilityClass
public class DateUtils {

    /**
     * Counts vacation days in period, weekends not included.
     *
     * @param dateFrom inclusive
     * @param dateTo inclusive
     * @return number of vacation days
     */
    public static int getVacationDaysInPeriod(LocalDate dateFrom, LocalDate dateTo) {

        List<LocalDate> vacationDaysYear = getVacationDayArray(dateFrom.getYear());
        if(dateFrom.getYear() != dateTo.getYear()) vacationDaysYear.addAll(getVacationDayArray(dateTo.getYear()));

        int countVacationDays = 0;
        for (LocalDate localDate : vacationDaysYear) {
            if (isWeekendDay(localDate)) continue;
            if(localDate.isAfter(dateFrom) && localDate.isBefore(dateTo)) countVacationDays++;
            if(localDate.isEqual(dateFrom) || localDate.isEqual(dateTo)) countVacationDays++;
        }

        return countVacationDays;
    }

    public static int getWeekdaysInPeriod(LocalDate dateFrom, LocalDate dateTo) {
        int weekDays = 0;

        LocalDate localDate = dateFrom;
        do {
            if(!isWeekendDay(localDate)) weekDays++;
            localDate = localDate.plusDays(1);
        } while (localDate.isBefore(dateTo.plusDays(1)));
        return weekDays - getVacationDaysInPeriod(dateFrom, dateTo);
    }

    public static boolean isWeekendDay(LocalDate localDate) {
        if(localDate.getDayOfWeek().equals(DayOfWeek.SATURDAY) || localDate.getDayOfWeek().equals(DayOfWeek.SUNDAY))
            return true;
        return false;
    }

    public static boolean isWorkday(LocalDate localDate) {
        if(isWeekendDay(localDate)) return false;
        for (LocalDate vacationDate : getVacationDayArray(localDate.getYear())) {
            if(localDate.isEqual(vacationDate)) return false;
        }
        return true;
    }

    private static List<LocalDate> getVacationDayArray(int year) {
        int a = year % 19;
        int b = (int) Math.round(year/100.0);
        int c = year % 100;
        int d = (int) Math.round(b/4.0);
        int e = b % 4;
        int f = (int) Math.floor((b+8.0)/25.0);
        int g = (int) Math.floor((b-f+1.0)/3.0);
        int h = (19*a+b-d-g+15) % 30;
        int j = (int) Math.floor(c/4.0);
        int k = c % 4;
        int l = (32+2*e+2*j-h-k) % 7;
        int m = (int) Math.floor((a+11.0*h+22.0*l)/451.0);
        int n = (int) Math.floor((h+l-7.0*m+114.0)/31.0);
        int p = (h+l-7*m+114) % 31;
        int q = (n-3)*31+p-20;

        int day = p+1;
        int month = n;

        LocalDate newYearsDay = LocalDate.of(year, 1, 1);
        LocalDate easterDay = LocalDate.of(year, month, day);
        LocalDate easterFriday = easterDay.minusDays(2);
        LocalDate easterThursday = easterDay.minusDays(3);
        LocalDate secondEasterday = easterDay.plusDays(1);
        LocalDate prayerday = easterDay.plusDays(26);
        LocalDate assendenceDay = easterDay.plusDays(39);
        LocalDate whitSun = easterDay.plusDays(49);
        LocalDate whitMon = easterDay.plusDays(50);
        LocalDate grundlovsDay = LocalDate.of(year, 6, 5);
        LocalDate christmasEve = LocalDate.of(year, 12, 24);
        LocalDate christmasDay = christmasEve.plusDays(1);
        LocalDate secondChristmasDay = christmasEve.plusDays(2);
        LocalDate newYearsEve = LocalDate.of(year, 12, 31);

        ArrayList<LocalDate> vacationDayList = new ArrayList<>();
        vacationDayList.add(newYearsDay);
        vacationDayList.add(easterThursday);
        vacationDayList.add(easterFriday);
        vacationDayList.add(easterDay);
        vacationDayList.add(secondEasterday);
        vacationDayList.add(prayerday);
        vacationDayList.add(assendenceDay);
        vacationDayList.add(whitSun);
        vacationDayList.add(whitMon);
        vacationDayList.add(grundlovsDay);
        vacationDayList.add(christmasEve);
        vacationDayList.add(christmasDay);
        vacationDayList.add(secondChristmasDay);
        vacationDayList.add(newYearsEve);

        return vacationDayList;
    }

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

    public static LocalDate getFirstDayOfMonth(int year, int month) {
        return getFirstDayOfMonth(LocalDate.of(year, month, 1));
    }

    public static LocalDate getLastDayOfMonth(int year, int month) {
        return getLastDayOfMonth(LocalDate.of(year, month, 1));
    }

    public static LocalDate getLastDayOfMonth(LocalDate localDate) {
        YearMonth month = YearMonth.from(localDate);
        return month.atEndOfMonth();
    }

    /**
     * Fra 1. jan til 5. jan giver 4 dage.
     *
     * @param dateBefore inclusive
     * @param dateAfter exclusive
     * @return
     */
    public static int countDaysBetween(LocalDate dateBefore, LocalDate dateAfter) {
        return Math.toIntExact(DAYS.between(dateBefore, dateAfter));
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

    public static String stringIt(LocalDate date) {
        return date.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
    }
}
