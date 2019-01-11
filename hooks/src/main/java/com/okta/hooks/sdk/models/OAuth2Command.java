package com.okta.hooks.sdk.models;

import java.util.Collections;
import java.util.Map;
import java.util.stream.Collectors;

public class OAuth2Command extends Command {

    private OAuth2Command(String type, Object value) {
        super(type, value);
    }

    public static OAuth2Command addIdTokenClaim(String key, Object value) {
        return addIdTokenClaims(Collections.singletonMap(key, value));
    }

    public static  OAuth2Command addIdTokenClaims(Map<String, Object> values) {
        return createAddCommand("com.okta.tokens.id_token.patch", values);
    }

    public static  OAuth2Command addAccessTokenClaim(String key, Object value) {
        return addAccessTokenClaims(Collections.singletonMap(key, value));
    }

    public static  OAuth2Command addAccessTokenClaims(Map<String, Object> values) {
        return createAddCommand("com.okta.tokens.access_token.patch", values);
    }


    static OAuth2Command createAddCommand(String type, Map<String, Object> values) {
        return new OAuth2Command(type, values.entrySet().stream()
                .map(entry -> new PatchOperation("add", "/claims/" + entry.getKey(), entry.getValue()))
                .collect(Collectors.toList()));
    }

}