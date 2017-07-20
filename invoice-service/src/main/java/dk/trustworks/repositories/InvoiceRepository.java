package dk.trustworks.repositories;

import dk.trustworks.model.Invoice;
import dk.trustworks.model.InvoiceStatus;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;
import org.springframework.data.rest.core.annotation.RestResource;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.Collection;
import java.util.List;

/**
 * Created by hans on 11/07/2017.
 */
@RepositoryRestResource(collectionResourceRel = "invoices", path = "invoices")
public interface InvoiceRepository extends PagingAndSortingRepository<Invoice, String> {
    List<Invoice> findByYearAndMonth(@Param("year") int year, @Param("month") int month);
    List<Invoice> findByStatus(@Param("status") InvoiceStatus status);
    List<Invoice> findByStatusIn(@Param("statuses") Collection<InvoiceStatus> statuses);

    @Query(value = "SELECT MAX(i.invoicenumber) FROM invoicemanager.invoices i", nativeQuery = true)
    Integer getMaxInvoiceNumber();

    //@Override @RestResource(exported = false) void delete(String id);
    //@Override @RestResource(exported = false) void delete(Invoice entity);

}

