--liquibase formatted sql

--changeset imponomarev:6
ALTER TABLE products ALTER COLUMN price DROP NOT NULL;
ALTER TABLE products ALTER COLUMN part_number DROP NOT NULL;