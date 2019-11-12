package dk.trustworks.invoicewebui.network.dto;

public class MarginResult {

    private int margin;

    public MarginResult() {
    }

    public MarginResult(int margin) {
        this.margin = margin;
    }

    public int getMargin() {
        return margin;
    }

    public void setMargin(int margin) {
        this.margin = margin;
    }
}
