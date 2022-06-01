package dk.trustworks.invoicewebui.network.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
        "margin"
})
public class MarginResult {

    @JsonProperty("margin")
    public int margin;

    public MarginResult() {
    }

    public MarginResult(int margin) {
        super();
        this.margin = margin;
    }

    public int getMargin() {
        return margin;
    }

    public void setMargin(int margin) {
        this.margin = margin;
    }

    @Override
    public String toString() {
        return "MarginResult{" +
                "margin=" + margin +
                '}';
    }
}
