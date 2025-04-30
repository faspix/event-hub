--liquibase formatted sql

--changeset faspix:1
ALTER TABLE comments
    ADD COLUMN updated_at TIMESTAMP WITH TIME ZONE DEFAULT NOW()


