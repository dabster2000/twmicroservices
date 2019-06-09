package dk.trustworks.invoicewebui.model;

import dk.trustworks.invoicewebui.model.enums.TalentPassionType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "talent_passion")
public class TalentPassion {

    @Id
    private String uuid;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="useruuid")
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="owner")
    private User owner;

    @Column(name = "type")
    @Enumerated(EnumType.STRING)
    private TalentPassionType type;

    private int performance;

    private int potential;

    private LocalDate registered;


}
