package com.faspix.notificationservice.utility;

import org.springframework.core.convert.converter.Converter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.jwt.Jwt;

import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class CustomJwtAuthenticationConverter implements Converter<Jwt, Collection<GrantedAuthority>> {
    private static final String ROLE_PREFIX = "ROLE_";
    private static final String CLAIM_REALM_ACCESS = "realm_access";
    private static final String CLAIM_ROLES = "roles";

    @Override
    public Collection<GrantedAuthority> convert(Jwt jwt) {
        Object realmAccess = jwt.getClaim(CLAIM_REALM_ACCESS);
        if (realmAccess instanceof java.util.Map<?, ?> realmAccessMap) {
            Object roles = realmAccessMap.get(CLAIM_ROLES);
            if (roles instanceof List<?> roleList) {
                Set<GrantedAuthority> authorities = roleList.stream()
                        .filter(role -> role instanceof String)
                        .map(role -> ROLE_PREFIX + ((String) role).toUpperCase())
                        .map(SimpleGrantedAuthority::new)
                        .collect(Collectors.toSet());
                return authorities;
            }
        }
        return Set.of();
    }

}
