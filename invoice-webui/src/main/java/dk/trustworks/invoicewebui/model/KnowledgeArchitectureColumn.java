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
@Table(name = "know_archi_column")
public class KnowledgeArchitectureColumn {

    @Id
    @GeneratedValue
    private int id;
    private String name;
    private String color;

    @OneToMany(fetch = FetchType.EAGER, cascade={CascadeType.ALL})
    @JoinColumn(name="col_id")
    public List<KnowledgeArchitectureCell> cells;
}
