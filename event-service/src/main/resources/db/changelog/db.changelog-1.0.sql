--liquibase formatted sql

--changeset faspix:1
CREATE TABLE events (
    event_id BIGINT GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,
    title VARCHAR(255) NOT NULL,
    annotation VARCHAR(255) NOT NULL,
    category_id BIGINT NOT NULL,
    confirmed_requests INTEGER NOT NULL,
    creation_date TIMESTAMP WITH TIME ZONE NOT NULL,
    description VARCHAR(255) NOT NULL,
    event_date TIMESTAMP NOT NULL,
    initiator_id VARCHAR(50) NOT NULL,
    lat DOUBLE PRECISION NOT NULL,
    lon DOUBLE PRECISION NOT NULL,
    paid BOOLEAN NOT NULL,
    participant_limit INTEGER NOT NULL,
    published_on TIMESTAMP WITH TIME ZONE,
    request_moderation BOOLEAN NOT NULL,
    state VARCHAR(255) NOT NULL,
    views INTEGER NOT NULL,
    CONSTRAINT events_state_check
        CHECK ((state)::TEXT = ANY
               ((ARRAY ['PENDING'::CHARACTER VARYING,
                   'PUBLISHED'::CHARACTER VARYING,
                   'CANCELED'::CHARACTER VARYING])::TEXT[]))
);