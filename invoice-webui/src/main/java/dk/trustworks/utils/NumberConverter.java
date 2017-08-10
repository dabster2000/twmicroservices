package dk.trustworks.utils;

import java.text.NumberFormat;
import java.util.Locale;

/**
 * Created by hans on 10/08/2017.
 */
public class NumberConverter {

    {
        Locale.setDefault(new Locale("da", "DK"));
    }

    public static NumberFormat getCurrencyInstance() {
        return NumberFormat.getCurrencyInstance();
    }

    public static String formatCurrency(double d) {
        return NumberFormat.getCurrencyInstance().format(d);
    }

}
