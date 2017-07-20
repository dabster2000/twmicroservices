ALTER TABLE invoicemanager.invoices CHANGE cvr_ean cvr VARCHAR(40);
ALTER TABLE invoicemanager.invoices ADD ean VARCHAR(40) NULL;
ALTER TABLE invoicemanager.invoices
  MODIFY COLUMN ean VARCHAR(40) AFTER zipcity;