package dk.trustworks.invoicewebui.model.enums;

import com.fasterxml.jackson.annotation.JsonEnumDefaultValue;

/**
 * Created by hans on 10/09/2017.
 */
public enum StatusType {

    ACTIVE {public String toString() {return "Active";}},
        @JsonEnumDefaultValue TERMINATED {public String toString() {return "Terminated";}},
    NON_PAY_LEAVE {public String toString() {return "On Leave";}}
}
