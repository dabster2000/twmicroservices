ALTER TABLE invoiceitems DROP FOREIGN KEY invoiceitems_invoices_uuid_fk;
ALTER TABLE invoiceitems
ADD CONSTRAINT invoiceitems_invoices_uuid_fk
FOREIGN KEY (invoiceuuid) REFERENCES invoices (uuid) ON DELETE CASCADE;