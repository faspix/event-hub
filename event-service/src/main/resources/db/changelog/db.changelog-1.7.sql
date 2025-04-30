--liquibase formatted sql

--changeset faspix:1
ALTER TABLE comments
    ADD COLUMN author_username VARCHAR(50);

--changeset faspix:2
UPDATE comments
    SET author_username = ''
    WHERE author_username IS NULL;

--changeset faspix:3
ALTER TABLE comments
    ALTER COLUMN author_username SET NOT NULL;
