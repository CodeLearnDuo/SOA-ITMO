--liquibase formatted sql

--changeset imponomarev:4
ALTER TABLE products
ALTER COLUMN name TYPE TEXT,
    ALTER COLUMN part_number TYPE TEXT;

--rollback ALTER TABLE products ALTER COLUMN name TYPE VARCHAR(255), ALTER COLUMN part_number TYPE VARCHAR(255);
