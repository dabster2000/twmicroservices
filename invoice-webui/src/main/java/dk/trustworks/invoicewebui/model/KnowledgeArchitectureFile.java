package dk.trustworks.invoicewebui.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "know_archi_file")
public class KnowledgeArchitectureFile {

    @Id
    @GeneratedValue
    private int id;
    private String headline;
    private String filetype;
    private String preview;
    private String filename;
    private String description;
    private String authors;
    private String customeruuid;
    private String projectuuid;
    private LocalDate date;

    @ManyToOne(fetch = FetchType.EAGER, cascade={CascadeType.ALL})
    @JoinColumn(name="card_id")
    private KnowledgeArchitectureCard knowledgeArchitectureCard;

    public KnowledgeArchitectureFile(String headline, String filetype, String preview, String filename, String description, String authors, LocalDate date) {
        this.headline = headline;
        this.filetype = filetype;
        this.preview = preview;
        this.filename = filename;
        this.description = description;
        this.authors = authors;
        this.date = date;
    }
}
