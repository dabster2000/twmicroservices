ALTER TABLE invoices ADD otheraddressinfo VARCHAR(150) NULL;
ALTER TABLE invoices
  MODIFY COLUMN otheraddressinfo VARCHAR(150) AFTER clientaddresse;