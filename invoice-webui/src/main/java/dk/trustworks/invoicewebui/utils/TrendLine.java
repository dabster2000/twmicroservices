package dk.trustworks.invoicewebui.utils;

public interface TrendLine {
    void setValues(double[] y, double[] x); // y ~ f(x)
    double predict(double x); // get a predicted y for a given x
}
