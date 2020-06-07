package dk.trustworks.invoicewebui;

import dk.trustworks.invoicewebui.utils.DateUtils;

import java.time.LocalDate;

/**
 * Created by hans on 31/08/2017.
 */
public class Test {

    public static void main(String[] args) throws Exception {
        int monthsBetween = DateUtils.countMonthsBetween(LocalDate.of(LocalDate.now().getYear(), 1, 1), LocalDate.of(2020, 3, 15).withDayOfMonth(1));
        int maxBudgetFirstYear = 24000 - (monthsBetween<0?0:(monthsBetween * 2000));
        System.out.println("maxBudgetFirstYear = " + maxBudgetFirstYear);
    }

}
