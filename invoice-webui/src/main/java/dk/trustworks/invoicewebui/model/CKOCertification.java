package dk.trustworks.invoicewebui.model;

import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

@Data
@NoArgsConstructor
@Entity
@Table(name = "cko_certifications")
public class CKOCertification {

    @Id
    @GeneratedValue
    private int id;

    private String name;

}
