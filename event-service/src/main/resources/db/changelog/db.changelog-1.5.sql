--liquibase formatted sql

--changeset faspix:1
ALTER TABLE events
    ADD COLUMN initiator_username VARCHAR(50);

--changeset faspix:2
UPDATE events
    SET initiator_username = ''
    WHERE initiator_username IS NULL;

--changeset faspix:3
ALTER TABLE events
    ALTER COLUMN initiator_username SET NOT NULL;
