package dk.trustworks.invoicewebui.model.rest.cryptocompare;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
        "time",
        "close",
        "high",
        "low",
        "open",
        "volumefrom",
        "volumeto"
})
public class Datum {

    @JsonProperty("time")
    private int time;
    @JsonProperty("close")
    private double close;
    @JsonProperty("high")
    private double high;
    @JsonProperty("low")
    private double low;
    @JsonProperty("open")
    private double open;
    @JsonProperty("volumefrom")
    private double volumefrom;
    @JsonProperty("volumeto")
    private double volumeto;

    @JsonProperty("time")
    public int getTime() {
        return time;
    }

    @JsonProperty("time")
    public void setTime(int time) {
        this.time = time;
    }

    @JsonProperty("close")
    public double getClose() {
        return close;
    }

    @JsonProperty("close")
    public void setClose(double close) {
        this.close = close;
    }

    @JsonProperty("high")
    public double getHigh() {
        return high;
    }

    @JsonProperty("high")
    public void setHigh(double high) {
        this.high = high;
    }

    @JsonProperty("low")
    public double getLow() {
        return low;
    }

    @JsonProperty("low")
    public void setLow(double low) {
        this.low = low;
    }

    @JsonProperty("open")
    public double getOpen() {
        return open;
    }

    @JsonProperty("open")
    public void setOpen(double open) {
        this.open = open;
    }

    @JsonProperty("volumefrom")
    public double getVolumefrom() {
        return volumefrom;
    }

    @JsonProperty("volumefrom")
    public void setVolumefrom(double volumefrom) {
        this.volumefrom = volumefrom;
    }

    @JsonProperty("volumeto")
    public double getVolumeto() {
        return volumeto;
    }

    @JsonProperty("volumeto")
    public void setVolumeto(double volumeto) {
        this.volumeto = volumeto;
    }
}
