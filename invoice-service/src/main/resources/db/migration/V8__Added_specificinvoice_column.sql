ALTER TABLE invoicemanager.invoices ADD specificdescription VARCHAR(255) NULL;
ALTER TABLE invoicemanager.invoices
  MODIFY COLUMN specificdescription VARCHAR(255) AFTER description;