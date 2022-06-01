package dk.trustworks.invoicewebui.web.admin.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Employee {
    String uuid;
    String role;
    String team;
    String name;
    String cpr;
    String age;
    String employedDate;
    String adresse;
    String phone;
    String email;
    String allocation;
    int monthSalary;
    int yearSalary;
    String pension;
    String healthcare;
    String photoconsent;
    String addedPension;
    String defects;
    String other;
    String status;
}
