--liquibase formatted sql

--changeset imponomarev:3
ALTER TABLE organizations
ALTER COLUMN name TYPE TEXT;

--rollback ALTER TABLE organizations ALTER COLUMN name TYPE VARCHAR(255);
