# Event hub

[![Codacy Badge](https://app.codacy.com/project/badge/Grade/1daac95c97d64c7aa9ebe534fd3ab57f)](https://app.codacy.com/gh/faspix/event-hub/dashboard?utm_source=gh&utm_medium=referral&utm_content=&utm_campaign=Badge_grade)

**Event Hub** is a microservices-based application that allows users to share interesting events and find companions to attend them together.

## Overview

The platform provides a space where users can:

* Discover and create events
* Browse events by categories and curated collections
* Request participation in events
* View event statistics and trends
* Connect with others who share similar interests

## Microservices Architecture


The system is built using a modular microservices architecture and includes the following components:

* **Event Service** - manages the creation, updating, and retrieval of event information.
* **Category Service** - handles categorization of events for better filtering and organization.
* **Compilation Service** - allows the creation of thematic collections or selections of events.
* **Request Service** - allows the creation of thematic collections or selections of events.
* **Statistics Service** - collects and provides analytics on events.
* **User Service** - manages user profiles and authentication data.
* **API Gateway** - serves as a single entry point for client interactions with the system.
* **Config Service** - provides centralized configuration management for all services.
* **Discovery Service** - enables dynamic service registration and discovery for scalability and resilience.

## Tech Stack

* REST API
* Spring Cloud
* Spring Boot
* Spring Security
* Spring Data JPA
* Spring Data JDBC
* Mapstruct
* Keycloak
* RabbitMQ
* Redis
* PostgreSQL
* Elasticsearch
* ELK Stack
* Prometheus
* Micrometer
* Grafana
* JUnit
* Liquibase 
* Docker

