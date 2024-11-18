package com.example.identity_service.configuration;

import lombok.extern.slf4j.Slf4j;
import org.keycloak.OAuth2Constants;
import org.keycloak.admin.client.Keycloak;
import org.keycloak.admin.client.KeycloakBuilder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@Slf4j
public class KeyCloakConfiguration {
    @Value("${keycloak.adminClientId}")
    private String adminClientId;

    @Value("${keycloak.adminClientSecret}")
    private String adminClientSecret;

    @Value("${keycloak.urls.auth}")
    private String authServerUrl;

    @Value("${keycloak.realm}")
    private String realm;

    @Bean
    public Keycloak keycloak() {
        log.info("Config KeyCloak Successfully");
        return KeycloakBuilder.builder()
                .serverUrl(authServerUrl)
                .realm(realm)
                .grantType(OAuth2Constants.CLIENT_CREDENTIALS)
                .clientId(adminClientId)
                .clientSecret(adminClientSecret)
                .build();
    }
}
