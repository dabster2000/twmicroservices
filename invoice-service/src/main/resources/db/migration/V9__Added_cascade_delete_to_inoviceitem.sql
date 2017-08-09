ALTER TABLE invoicemanager.invoiceitems DROP FOREIGN KEY invoiceitems_invoices_uuid_fk;
ALTER TABLE invoicemanager.invoiceitems
ADD CONSTRAINT invoiceitems_invoices_uuid_fk
FOREIGN KEY (invoiceuuid) REFERENCES invoices (uuid) ON DELETE CASCADE;