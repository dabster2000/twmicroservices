create table invoices
(
	uuid varchar(40) not null
		primary key,
	clientname varchar(150) null,
	clientaddresse varchar(200) null,
	zipcity varchar(100) null,
	cvr_ean varchar(40) null,
	attention varchar(150) null,
	invoicenumber int null,
	invoicedate date null,
	description varchar(200) null,
	status varchar(40) null,
	projectuuid varchar(40) null,
	projectname varchar(100) null,
	year int null,
	month int null,
	type varchar(40) null,
	constraint invoices_uuid_uindex
		unique (uuid),
	constraint invoices_projectuuid_uindex
		unique (projectuuid)
)
;

create index invoices_year_month_projectuuid_index
	on invoices (year, month, projectuuid)
;

create table invoiceitems
(
	uuid varchar(40) not null
		primary key,
	itemname varchar(100) null,
	description varchar(200) null,
	consultant varchar(150) null,
	rate double null,
	hours double null,
	invoiceuuid varchar(40) null,
	constraint invoiceitems_invoices_uuid_fk
		foreign key (invoiceuuid) references invoicemanager.invoices (uuid)
)
;

create index invoiceitems_invoiceuuid_index
	on invoiceitems (invoiceuuid)
;


