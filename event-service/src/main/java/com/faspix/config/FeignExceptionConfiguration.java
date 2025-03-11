package com.faspix.config;

import com.faspix.exception.ExceptionResponse;
import com.faspix.exception.ResourceNotFoundException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import feign.Response;
import feign.codec.ErrorDecoder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatus;

import java.io.IOException;
import java.io.InputStream;

@Configuration
public class FeignExceptionConfiguration {

    @Bean
    ErrorDecoder errorDecoder() {
        return new ClientErrorDecoder();
    }


    private static class ClientErrorDecoder implements ErrorDecoder {

        @Override
        public Exception decode(String methodKey, Response response) {
            ExceptionResponse exceptionResponse;
            try (InputStream bodyIs = response.body().asInputStream()) {
                ObjectMapper mapper = new ObjectMapper();
                mapper.registerModule(new JavaTimeModule());
                exceptionResponse = mapper.readValue(bodyIs, ExceptionResponse.class);
            } catch (IOException e) {
                return new RuntimeException(e.getMessage());
            }

            return switch (HttpStatus.resolve(response.status())) {
                case NOT_FOUND -> new ResourceNotFoundException(exceptionResponse.getMessage());
                case null -> new RuntimeException("Unknown error occurred " + methodKey);
                default -> new RuntimeException(exceptionResponse.getMessage());
            };
        }

    }

}
