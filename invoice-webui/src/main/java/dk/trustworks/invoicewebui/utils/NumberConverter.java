package dk.trustworks.invoicewebui.utils;

import java.text.NumberFormat;
import java.text.ParseException;
import java.util.Locale;

/**
 * Created by hans on 10/08/2017.
 */
public class NumberConverter {


    public static NumberFormat getCurrencyInstance() {
        return NumberFormat.getCurrencyInstance();
    }

    public static String formatCurrency(double d) {
        return NumberFormat.getCurrencyInstance().format(d);
    }

    public static double parseDouble(String d) {
        try {
            return NumberFormat.getInstance().parse(d).doubleValue();
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return 0.0;
    }

    public static String formatDouble(double d) {
        NumberFormat numberInstance = NumberFormat.getNumberInstance();
        numberInstance.setMinimumFractionDigits(2);
        numberInstance.setMaximumFractionDigits(2);
        return numberInstance.format(d);
    }

    public static NumberFormat getDoubleInstance() {
        return NumberFormat.getNumberInstance();
    }

}
