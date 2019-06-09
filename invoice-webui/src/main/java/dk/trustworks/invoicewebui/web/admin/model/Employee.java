package dk.trustworks.invoicewebui.web.admin.model;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class Employee {
    String name;
    String status;
    int hours;
    int salary;
}
