package com.faspix.config;

import feign.RequestInterceptor;
import feign.RequestTemplate;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.oauth2.client.OAuth2AuthorizeRequest;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClient;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientManager;

@Configuration
@RequiredArgsConstructor
public class FeignConfiguration implements RequestInterceptor {

    private final OAuth2AuthorizedClientManager authorizedClientManager;

    @Override
    public void apply(RequestTemplate requestTemplate) {
        OAuth2AuthorizeRequest authorizeRequest = OAuth2AuthorizeRequest
                .withClientRegistrationId("keycloak")
                .principal("microservice-client")
                .build();

        OAuth2AuthorizedClient authorizedClient = authorizedClientManager.authorize(authorizeRequest);

        if (authorizedClient != null && authorizedClient.getAccessToken() != null) {
            String token = authorizedClient.getAccessToken().getTokenValue();
            requestTemplate.header("Authorization", "Bearer " + token);
        }
    }
}
