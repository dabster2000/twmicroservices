package dk.trustworks.invoicewebui.repositories;

/**
 * Created by hans on 11/07/2017.
 */
/*
@RepositoryRestResource(collectionResourceRel = "invoices", path = "invoices")
public interface InvoiceRepository extends CrudRepository<Invoice, String> {

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

    @Query(value = "SELECT * FROM invoices i WHERE " +
            "i.invoicedate >= :periodStart AND i.invoicedate <= :periodEnd " +
            " AND i.bookingdate = '1900-01-01' " +
            "AND i.status IN ('CREATED', 'CREDIT_NOTE');", nativeQuery = true)
    List<Invoice> findByInvoicedate(@Param("periodStart") String periodStart, @Param("periodEnd") String periodEnd);

    @Query(value = "SELECT * FROM invoices i WHERE " +
            "i.bookingdate >= :periodStart AND i.bookingdate <= :periodEnd " +
            "AND i.status IN ('CREATED', 'CREDIT_NOTE');", nativeQuery = true)
    List<Invoice> findByBookingdate(@Param("periodStart") String periodStart, @Param("periodEnd") String periodEnd);

    @Query(value = "SELECT MAX(i.invoicenumber) FROM invoices i", nativeQuery = true)
    Integer getMaxInvoiceNumber();

    List<Invoice> findAll();




}

 */

