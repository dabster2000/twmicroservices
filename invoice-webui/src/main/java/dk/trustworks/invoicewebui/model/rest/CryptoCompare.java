package dk.trustworks.invoicewebui.model.rest;


import com.fasterxml.jackson.annotation.JsonProperty;

public class CryptoCompare {

    private double btc;
    private double usd;
    private double eur;

    public CryptoCompare() {
    }

    public CryptoCompare(double btc, double usd, double eur) {
        this.btc = btc;
        this.usd = usd;
        this.eur = eur;
    }

    @JsonProperty("BTC")
    public double getBtc() {
        return btc;
    }

    public void setBtc(double btc) {
        this.btc = btc;
    }

    @JsonProperty("USD")
    public double getUsd() {
        return usd;
    }

    public void setUsd(double usd) {
        this.usd = usd;
    }

    @JsonProperty("EUR")
    public double getEur() {
        return eur;
    }

    public void setEur(double eur) {
        this.eur = eur;
    }

    @Override
    public String toString() {
        return "CryptoCompare{" +
                "btc=" + btc +
                ", usd=" + usd +
                ", eur=" + eur +
                '}';
    }
}
