ALTER TABLE invoicemanager.invoices ADD otheraddressinfo VARCHAR(150) NULL;
ALTER TABLE invoicemanager.invoices
  MODIFY COLUMN otheraddressinfo VARCHAR(150) AFTER clientaddresse;