package dk.trustworks.invoicewebui.functions;

public interface TokenEventListener {

    void onTokenAdded(String token);
    void onTokenRemoved(String token);

}
