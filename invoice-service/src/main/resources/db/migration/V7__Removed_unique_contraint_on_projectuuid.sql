DROP INDEX invoices_projectuuid_uindex ON invoicemanager.invoices;
CREATE INDEX invoices_projectuuid_index ON invoicemanager.invoices (projectuuid);