package dk.trustworks.invoicewebui.utils;

import lombok.experimental.UtilityClass;

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

    /**
     * Both date inclusive
     * @param testDate
     * @param from
     * @param to
     * @return
     */
    public static boolean isBetween(LocalDate testDate, LocalDate from, LocalDate to) {
        if(testDate.isEqual(from)) return true;
        if(testDate.isEqual(to)) return true;
        if(testDate.isAfter(from) && testDate.isBefore(to)) return true;
        return false;
    }

    public static int getWeekdaysInPeriod(LocalDate dateFrom, LocalDate dateTo) {
        int weekDays = 0;

        LocalDate localDate = dateFrom;
        do {
            if(!isWeekendDay(localDate)) weekDays++;
            localDate = localDate.plusDays(1);
        } while (localDate.isBefore(dateTo));
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

    public static String stringIt(LocalDate date) {
        return date.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
    }

    public static String stringIt(LocalDate date, String format) {
        return date.format(DateTimeFormatter.ofPattern(format));
    }

    public static LocalDate getCurrentFiscalStartDate() {
        return (LocalDate.now().getMonthValue()>6 && LocalDate.now().getMonthValue()<13)?LocalDate.now().withMonth(7).withDayOfMonth(1):LocalDate.now().minusYears(1).withMonth(7).withDayOfMonth(1);
    }
}
