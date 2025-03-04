--liquibase formatted sql

--changeset faspix:1
CREATE TABLE compilations (
    id BIGINT GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,
    title VARCHAR(255) NOT NULL UNIQUE,
    pinned BOOLEAN NOT NULL
);

--changeset faspix:2
CREATE TABLE compilation_events (
    compilation_id BIGINT NOT NULL,
    event_id BIGINT NOT NULL,
    CONSTRAINT fk_compilation
        FOREIGN KEY(compilation_id)
        REFERENCES compilations(id)
);
