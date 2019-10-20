package dk.trustworks.invoicewebui.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "know_archi_cell")
public class KnowledgeArchitectureCell {

    @Id
    @GeneratedValue
    private int id;
    private String name;
    private String description;
    private String photoUuid;
    @Lob
    private String content;

    @Column(name = "row_order")
    private int rowOrder;

    @OneToMany(fetch = FetchType.EAGER, cascade={CascadeType.ALL})
    @JoinColumn(name="cell_id")
    public List<KnowledgeArchitectureCard> cards;
}
