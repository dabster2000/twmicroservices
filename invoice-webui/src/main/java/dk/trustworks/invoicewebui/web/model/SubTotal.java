package dk.trustworks.invoicewebui.web.model;

/**
 * Created by hans on 17/07/2017.
 */
public class SubTotal {

    private int year;
    public int jan;
    public int feb;
    public int mar;
    public int apr;
    public int may;
    public int jun;
    public int jul;
    public int aug;
    public int sep;
    public int oct;
    public int nov;
    public int dec;
    private int sumnotax;
    private int sumwithtax;

    public SubTotal() {
    }

    public SubTotal(int year) {
        this.year = year;
    }

    public SubTotal(int year, int jan, int feb, int mar, int apr, int may, int jun, int jul, int aug, int sep, int oct, int nov, int dec) {
        this.year = year;
        this.jan = jan;
        this.feb = feb;
        this.mar = mar;
        this.apr = apr;
        this.may = may;
        this.jun = jun;
        this.jul = jul;
        this.aug = aug;
        this.sep = sep;
        this.oct = oct;
        this.nov = nov;
        this.dec = dec;
    }

    public int getYear() {
        return year;
    }

    public void setYear(int year) {
        this.year = year;
    }

    public int getJan() {
        return jan;
    }

    public void setJan(int jan) {
        this.jan = jan;
    }

    public int getFeb() {
        return feb;
    }

    public void setFeb(int feb) {
        this.feb = feb;
    }

    public int getMar() {
        return mar;
    }

    public void setMar(int mar) {
        this.mar = mar;
    }

    public int getApr() {
        return apr;
    }

    public void setApr(int apr) {
        this.apr = apr;
    }

    public int getMay() {
        return may;
    }

    public void setMay(int may) {
        this.may = may;
    }

    public int getJun() {
        return jun;
    }

    public void setJun(int jun) {
        this.jun = jun;
    }

    public int getJul() {
        return jul;
    }

    public void setJul(int jul) {
        this.jul = jul;
    }

    public int getAug() {
        return aug;
    }

    public void setAug(int aug) {
        this.aug = aug;
    }

    public int getSep() {
        return sep;
    }

    public void setSep(int sep) {
        this.sep = sep;
    }

    public int getOct() {
        return oct;
    }

    public void setOct(int oct) {
        this.oct = oct;
    }

    public int getNov() {
        return nov;
    }

    public void setNov(int nov) {
        this.nov = nov;
    }

    public int getDec() {
        return dec;
    }

    public void setDec(int dec) {
        this.dec = dec;
    }

    public int getSumnotax() {
        return sumnotax;
    }

    public void setSumnotax(int sumnotax) {
        this.sumnotax = sumnotax;
    }

    public int getSumwithtax() {
        return sumwithtax;
    }

    public void setSumwithtax(int sumwithtax) {
        this.sumwithtax = sumwithtax;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        SubTotal subTotal = (SubTotal) o;

        if (year != subTotal.year) return false;
        return dec == subTotal.dec;
    }

    @Override
    public int hashCode() {
        return year;
    }

    @Override
    public String toString() {
        return "SubTotal{" + "year=" + year +
                ", jan=" + jan +
                ", feb=" + feb +
                ", mar=" + mar +
                ", apr=" + apr +
                ", may=" + may +
                ", jun=" + jun +
                ", jul=" + jul +
                ", aug=" + aug +
                ", sep=" + sep +
                ", oct=" + oct +
                ", nov=" + nov +
                ", dec=" + dec +
                ", sumnotax=" + sumnotax +
                ", sumwithtax=" + sumwithtax +
                '}';
    }
}
