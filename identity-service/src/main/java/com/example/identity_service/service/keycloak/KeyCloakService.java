package com.example.identity_service.service.keycloak;


import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.experimental.NonFinal;
import lombok.extern.slf4j.Slf4j;
import org.keycloak.admin.client.Keycloak;
import org.keycloak.representations.idm.UserRepresentation;
import org.springframework.stereotype.Service;
import org.keycloak.admin.client.resource.*;
import org.springframework.beans.factory.annotation.Value;

import java.util.ArrayList;
import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE,makeFinal = true)
public class KeyCloakService {
    Keycloak keycloak;

    @NonFinal
    @Value("${keycloak.realm}")
    protected String realm;


    private UsersResource getUsersResource() {
        RealmResource realm1 = keycloak.realm(realm);
        return realm1.users();
    }


    public UserRepresentation getUserById(String userId) {
        log.info("User id: {}", userId);
        return getUsersResource().get(userId).toRepresentation();
    }

    public void emailVerification(String userId){
        UsersResource usersResource = getUsersResource();
        usersResource.get(userId).sendVerifyEmail();
    }

    public UserResource getUserResource(String userId){
        UsersResource usersResource = getUsersResource();
        return usersResource.get(userId);
    }


    public void updatePassword(String userId) {

        UserResource userResource = getUserResource(userId);
        List<String> actions= new ArrayList<>();
        actions.add("UPDATE_PASSWORD");
        userResource.executeActionsEmail(actions);

    }
}
