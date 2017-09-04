ALTER TABLE invoices ADD specificdescription VARCHAR(255) NULL;
ALTER TABLE invoices
  MODIFY COLUMN specificdescription VARCHAR(255) AFTER description;