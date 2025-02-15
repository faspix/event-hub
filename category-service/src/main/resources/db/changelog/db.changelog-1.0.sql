--liquibase formatted sql

--changeset faspix:1
CREATE TABLE categories (
    category_id BIGINT GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,
    name VARCHAR(255) NOT NULL UNIQUE
);