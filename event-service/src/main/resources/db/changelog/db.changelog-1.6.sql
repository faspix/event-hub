--liquibase formatted sql

--changeset faspix:1
ALTER TABLE events
    ADD COLUMN category_name VARCHAR(50);

--changeset faspix:2
UPDATE events
    SET category_name = ''
    WHERE category_name IS NULL;

--changeset faspix:3
ALTER TABLE events
    ALTER COLUMN category_name SET NOT NULL;
