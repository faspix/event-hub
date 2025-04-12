--liquibase formatted sql

--changeset faspix:1
ALTER TABLE events DROP COLUMN views;
