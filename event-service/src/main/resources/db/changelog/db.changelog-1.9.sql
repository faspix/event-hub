--liquibase formatted sql

--changeset faspix:1
ALTER TABLE comments
    RENAME COLUMN creation_date TO created_at;


