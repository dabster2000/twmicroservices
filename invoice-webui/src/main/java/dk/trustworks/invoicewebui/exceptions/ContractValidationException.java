package dk.trustworks.invoicewebui.exceptions;

public class ContractValidationException extends Exception {

    public ContractValidationException() {
    }

    public ContractValidationException(String message) {
        super(message);
    }

    public ContractValidationException(String message, Throwable cause) {
        super(message, cause);
    }

    public ContractValidationException(Throwable cause) {
        super(cause);
    }

    public ContractValidationException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
