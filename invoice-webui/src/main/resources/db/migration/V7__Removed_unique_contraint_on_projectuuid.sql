DROP INDEX invoices_projectuuid_uindex ON invoices;
CREATE INDEX invoices_projectuuid_index ON invoices (projectuuid);