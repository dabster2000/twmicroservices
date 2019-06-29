package dk.trustworks.invoicewebui.network.dto;

public class IntegerJsonResponse {

    private int result;

    public IntegerJsonResponse() {
    }

    public IntegerJsonResponse(int result) {
        this.result = result;
    }

    public int getResult() {
        return result;
    }

    public void setResult(int result) {
        this.result = result;
    }
}
