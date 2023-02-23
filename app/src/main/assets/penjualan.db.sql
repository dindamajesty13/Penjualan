BEGIN TRANSACTION;
DROP TABLE IF EXISTS "Login";
CREATE TABLE IF NOT EXISTS "Login" (
	"User"	TEXT,
	"Password"	TEXT
);
DROP TABLE IF EXISTS "Product";
CREATE TABLE IF NOT EXISTS "Product" (
	"ProductCode"	NUMERIC,
	"ProductName"	TEXT,
	"Price"	NUMERIC,
	"Currency"	TEXT,
	"Discount"	NUMERIC,
	"Dimension"	TEXT,
	"Unit"	TEXT
);
DROP TABLE IF EXISTS "TransactionHeadaer";
CREATE TABLE IF NOT EXISTS "TransactionHeadaer" (
	"DocumentCode"	TEXT,
	"DocumentNumber"	TEXT,
	"User"	TEXT,
	"Total"	NUMERIC,
	"Date"	TEXT
);
DROP TABLE IF EXISTS "TransactionDetail";
CREATE TABLE IF NOT EXISTS "TransactionDetail" (
	"DocumentCode"	TEXT,
	"DocumentNumber"	TEXT,
	"ProductCode"	TEXT,
	"Price"	NUMERIC,
	"Quantity"	INTEGER,
	"Unit"	TEXT,
	"SubTotal"	NUMERIC,
	"Currency"	TEXT
);
INSERT INTO "Login" VALUES ('Smit','_smit_OK');
INSERT INTO "Product" VALUES ('SKUSKILNP','So Klin Pewangi',15000,'IDR',10,'13 cm * 10 cm','PCS');
INSERT INTO "Product" VALUES ('GIVBIRU','Giv Biru',11000,'IDR',0,'13 cm * 10 cm','PCS');
INSERT INTO "Product" VALUES ('SOKLINLQID','So Klin Liquid',18000,'IDR',0,'13 cm * 10 cm','PCS');
INSERT INTO "Product" VALUES ('GIVKUNING','Giv Kuning',10000,'IDR',0,'13 cm * 10 cm','PCS');
COMMIT;
