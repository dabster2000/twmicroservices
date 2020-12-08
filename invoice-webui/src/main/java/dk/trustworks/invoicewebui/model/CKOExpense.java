package dk.trustworks.invoicewebui.model;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateSerializer;
import dk.trustworks.invoicewebui.model.enums.CKOExpensePurpose;
import dk.trustworks.invoicewebui.model.enums.CKOExpenseStatus;
import dk.trustworks.invoicewebui.model.enums.CKOExpenseType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CKOExpense {

    private String uuid;

    @JsonSerialize(using = LocalDateSerializer.class)
    @JsonDeserialize(using = LocalDateDeserializer.class)
    private LocalDate eventdate;

    private String useruuid;

    private String description;

    private int price;

    private String comment;

    private double days;

    private CKOExpenseType type;

    private CKOExpenseStatus status;

    private CKOExpensePurpose purpose;

    private double rating;

    private String rating_comment;

    private int certification;
    private int certified;

    public CKOExpense(User user) {
        this.useruuid = user.getUuid();
    }

}
