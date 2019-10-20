package dk.trustworks.invoicewebui.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.util.Set;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "know_archi_card")
public class KnowledgeArchitectureCard {

    @Id
    @GeneratedValue
    private int id;
    private String name;
    private String folder;

    @Column(name = "card_order")
    private int cardOrder;

}
