package confg;

import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtDecoder;

import java.util.List;
import java.util.Map;

@TestConfiguration
public class TestSecurityConfiguration {

    @Bean
    public JwtDecoder jwtDecoder() {
        return token -> Jwt.withTokenValue("mock-token")
                .header("alg", "RS256")
                .claim("sub", "1")
                .claim("username", "testUser")
                .claim("realm_access", Map.of("roles", List.of("USER", "ADMIN")))
                .build();
    }


}
