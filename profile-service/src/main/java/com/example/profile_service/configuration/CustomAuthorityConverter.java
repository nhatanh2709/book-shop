package com.example.profile_service.configuration;

import org.springframework.core.convert.converter.Converter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.jwt.Jwt;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class CustomAuthorityConverter implements Converter<Jwt, Collection<GrantedAuthority>> {
    private final String REALM_ACCESS = "realm_access";
    private final String ROLE_PREFIX = "ROLE_";

    @Override
    public Collection<GrantedAuthority> convert(Jwt source) {
        Map<String, Object> realmAccessMap = source.getClaimAsMap(REALM_ACCESS);
        Object roles = realmAccessMap.get("roles");
        if (roles instanceof List stringRoles) {
            return ((List<String>) stringRoles)
                    .stream()
                            .map(s -> new SimpleGrantedAuthority(String.format("%s%s", ROLE_PREFIX, s)))
                            .collect(Collectors.toList());
        }
        return List.of();
    }
}
