package com.faspix.config;

import co.elastic.clients.json.JsonpMapper;
import co.elastic.clients.json.jackson.JacksonJsonpMapper;
import co.elastic.clients.transport.rest_client.RestClientOptions;
import co.elastic.clients.transport.rest_client.RestClientTransport;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.elasticsearch.client.RestClient;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ElasticSearchConfig {

    @Bean
    RestClientTransport restClientTransport(RestClient restClient, JsonpMapper jsonMapper, ObjectProvider<RestClientOptions> restClientOptions) {
        ObjectMapper mapper = new ObjectMapper().registerModule(new JavaTimeModule());
        return new RestClientTransport(restClient,  new JacksonJsonpMapper(mapper), (RestClientOptions)restClientOptions.getIfAvailable());
    }

}
