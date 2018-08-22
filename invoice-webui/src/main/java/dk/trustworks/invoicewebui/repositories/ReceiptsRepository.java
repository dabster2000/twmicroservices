package dk.trustworks.invoicewebui.repositories;

/**
 * Created by hans on 27/06/2017.
 */

import dk.trustworks.invoicewebui.model.Project;
import dk.trustworks.invoicewebui.model.Receipt;
import dk.trustworks.invoicewebui.model.User;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

import java.time.LocalDate;
import java.util.List;

@RepositoryRestResource(collectionResourceRel = "receipts", path="receipts")
public interface ReceiptsRepository extends CrudRepository<Receipt, Integer> {

    List<Receipt> findByUserAndReceiptdateIsBetween(User user, LocalDate fromDate, LocalDate toDate);
    List<Receipt> findByProjectAndReceiptdateIsBetween(Project project, LocalDate fromDate, LocalDate toDate);
    List<Receipt> findByReceiptdateIsBetween(LocalDate fromDate, LocalDate toDate);

}
