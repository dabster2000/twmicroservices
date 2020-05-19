package dk.trustworks.invoicewebui.repositories;

/**
 * Created by hans on 27/06/2017.
 */

import dk.trustworks.invoicewebui.model.Project;
import dk.trustworks.invoicewebui.model.Receipt;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

import java.time.LocalDate;
import java.util.List;

@RepositoryRestResource(collectionResourceRel = "receipts", path="receipts")
public interface ReceiptsRepository extends CrudRepository<Receipt, Integer> {

    List<Receipt> findByUseruuidAndReceiptdateIsBetween(String user, LocalDate fromDate, LocalDate toDate);
    List<Receipt> findByProjectuuidAndReceiptdateIsBetween(String projectuuid, LocalDate fromDate, LocalDate toDate);
    List<Receipt> findByReceiptdateIsBetween(LocalDate fromDate, LocalDate toDate);

}
