package com.faspix.config;

import org.keycloak.admin.client.Keycloak;
import org.keycloak.admin.client.KeycloakBuilder;
import org.keycloak.admin.client.resource.RealmResource;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class KeycloakConfiguration {

    @Bean
    Keycloak keycloak(
        @Value("${keycloak.server-url}") String serverUrl,
        @Value("${keycloak.realm}") String realm,
        @Value("${keycloak.client-id}") String clientId,
        @Value("${keycloak.admin-username}") String adminUsername,
        @Value("${keycloak.admin-password}") String adminPassword
    ) {
        return KeycloakBuilder.builder()
            .serverUrl(serverUrl)
            .realm(realm)
            .clientId(clientId)
            .username(adminUsername)
            .password(adminPassword)
            .build();
    }

    @Bean
    RealmResource realmResource(
            Keycloak keycloak,
            @Value("${keycloak.realm}") String realm
    ) {
        return keycloak.realm(realm);
    }

}
