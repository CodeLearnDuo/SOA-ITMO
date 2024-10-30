--liquibase formatted sql

--changeset imponomarev:2
CREATE TABLE products
(
    id                            SERIAL PRIMARY KEY,
    name                          VARCHAR(255) NOT NULL CHECK (LENGTH(name) >= 1),
    x                             INT,
    y                             INT CHECK (y <= 398),
    creation_date                 TIMESTAMP    NOT NULL,
    price                         DOUBLE PRECISION CHECK (price > 0),
    part_number                   VARCHAR(255) UNIQUE CHECK (LENGTH(part_number) >= 19),
    unit_of_measure               VARCHAR(50)  NOT NULL CHECK (unit_of_measure IN ('CENTIMETERS', 'SQUARE_METERS', 'PCS', 'GRAMS')),
    manufacturer_id               INT          NOT NULL,
    FOREIGN KEY (manufacturer_id) REFERENCES organizations (id)
);
--rollback DROP TABLE products;