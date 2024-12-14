--liquibase formatted sql

--changeset imponomarev:5
ALTER TABLE products
DROP CONSTRAINT products_part_number_check;