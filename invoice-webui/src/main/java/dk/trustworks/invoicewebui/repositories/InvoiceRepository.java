package dk.trustworks.invoicewebui.repositories;

import dk.trustworks.invoicewebui.model.Invoice;
import dk.trustworks.invoicewebui.model.enums.InvoiceStatus;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

import java.time.LocalDate;
import java.util.List;

/**
 * Created by hans on 11/07/2017.
 */
@RepositoryRestResource(collectionResourceRel = "invoices", path = "invoices")
public interface InvoiceRepository extends CrudRepository<Invoice, String> {
    /**
     *
     * @param year the year
     * @param month the month-of-year, from 0 to 11
     * @return
     */
    List<Invoice> findByYearAndMonth(@Param("year") int year, @Param("month") int month);

    @Query(value = "SELECT COALESCE(SUM(ii.hours * ii.rate), 0) AS result FROM invoices i LEFT JOIN invoiceitems ii ON i.uuid = ii.invoiceuuid " +
            "WHERE i.invoicedate >= :periodStart AND i.invoicedate <= :periodEnd AND i.type = 0;", nativeQuery = true)
    double invoicedAmountByPeriod(@Param("periodStart") String periodStart, @Param("periodEnd") String periodEnd);

    @Query(value = "SELECT COALESCE(SUM(ii.hours * ii.rate), 0) AS result FROM invoices i LEFT JOIN invoiceitems ii ON i.uuid = ii.invoiceuuid " +
            "WHERE i.invoicedate >= :periodStart AND i.invoicedate <= :periodEnd AND i.type = 1;", nativeQuery = true)
    double creditNoteAmountByPeriod(@Param("periodStart") String periodStart, @Param("periodEnd") String periodEnd);

    List<Invoice> findByStatus(@Param("status") InvoiceStatus status);
    List<Invoice> findByStatusIn(@Param("statuses") InvoiceStatus... statuses);

    @Query(value = "SELECT * FROM invoices i " +
            "WHERE i.projectuuid LIKE :projectuuid " +
            "AND i.status IN ('CREATED', 'SUBMITTED', 'PAID', 'CREDIT_NOTE') " +
            "order by i.year, i.month desc " +
            "LIMIT 1", nativeQuery = true)
    Invoice findByLatestInvoiceByProjectuuid(@Param("projectuuid") String projectuuid);

    @Query(value = "select * from invoices i where ( " +
            "(i.invoicedate >= :searchdate and i.invoicedate <= :searchdate) " +
            " or (i.bookingdate >= :searchdate and i.bookingdate <= :searchdate) " +
            ") " +
            "and i.status IN :statuses; ")
    List<Invoice> findByInvoicedateOrBookingdateAndStatuses(LocalDate date, InvoiceStatus... statuses);

    @Query(value = "SELECT MAX(i.invoicenumber) FROM invoices i", nativeQuery = true)
    Integer getMaxInvoiceNumber();

    List<Invoice> findAll();


}

