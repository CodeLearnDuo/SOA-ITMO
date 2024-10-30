--liquibase formatted sql

--changeset imponomarev:1
CREATE TABLE organizations
(
    id                SERIAL PRIMARY KEY,
    name              VARCHAR(255) NOT NULL CHECK (LENGTH(name) >= 1),
    employees_count   INT CHECK (employees_count >= 1),
    organization_type VARCHAR(50)  NOT NULL CHECK (organization_type IN
                                                   ('COMMERCIAL', 'PUBLIC', 'GOVERNMENT', 'PRIVATE_LIMITED_COMPANY',
                                                    'OPEN_JOINT_STOCK_COMPANY'))
);
--rollback DROP TABLE organizations;