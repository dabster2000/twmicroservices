ALTER TABLE invoices CHANGE cvr_ean cvr VARCHAR(40);
ALTER TABLE invoices ADD ean VARCHAR(40) NULL;
ALTER TABLE invoices
  MODIFY COLUMN ean VARCHAR(40) AFTER zipcity;