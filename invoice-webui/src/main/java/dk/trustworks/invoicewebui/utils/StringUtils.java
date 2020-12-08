package dk.trustworks.invoicewebui.utils;

import java.text.NumberFormat;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;
import java.util.function.Predicate;

public class StringUtils {

    public static String convertInvoiceNumberToString(int number) {
        return NumberFormat.getNumberInstance()
                .format(number)
                .replace(",", "-")
                .replace(".", "-");
    }

    public static <T> Predicate<T> distinctByKey(Function<? super T, ?> keyExtractor) {
        Set<Object> seen = ConcurrentHashMap.newKeySet();
        return t -> seen.add(keyExtractor.apply(t));
    }

}
