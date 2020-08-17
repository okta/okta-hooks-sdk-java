/*
 * Copyright 2018-Present Okta, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.okta.hooks.sdk.commands;

import java.util.Arrays;
import java.util.Collections;
import java.util.Map;
import java.util.stream.Collectors;

public class OAuth2Command extends Command {

    private static final String ACCESS_TOKEN_PATCH = "com.okta.access.patch";
    private static final String ID_TOKEN_PATCH = "com.okta.identity.patch";

    private OAuth2Command(String type, Object value) {
        super(type, value);
    }

    public static OAuth2Command addIdTokenClaim(String key, Object value) {
        return addIdTokenClaims(Collections.singletonMap(key, value));
    }

    public static OAuth2Command addIdTokenClaims(Map<String, Object> values) {
        return createAddCommand(ID_TOKEN_PATCH, values);
    }

    public static OAuth2Command setIdTokenExpiration(int expiration) {
        return createExpirationCommand(ID_TOKEN_PATCH, expiration);
    }

    public static OAuth2Command replaceIdTokenClaim(String key, Object value) {
        return replaceIdTokenClaims(Collections.singletonMap(key, value));
    }

    public static OAuth2Command replaceIdTokenClaims(Map<String, Object> values) {
        return createReplaceCommand(ID_TOKEN_PATCH, values);
    }

    public static OAuth2Command removeIdTokenClaims(String... keys) {
        return createRemoveCommand(ID_TOKEN_PATCH, keys);
    }

    public static OAuth2Command addAccessTokenClaim(String key, Object value) {
        return addAccessTokenClaims(Collections.singletonMap(key, value));
    }

    public static OAuth2Command addAccessTokenClaims(Map<String, Object> values) {
        return createAddCommand(ACCESS_TOKEN_PATCH, values);
    }

    public static OAuth2Command setAccessTokenExpiration(int expiration) {
        return createExpirationCommand(ACCESS_TOKEN_PATCH, expiration);
    }

    public static OAuth2Command replaceAccessTokenClaim(String key, Object value) {
        return replaceAccessTokenClaims(Collections.singletonMap(key, value));
    }

    public static OAuth2Command replaceAccessTokenClaims(Map<String, Object> values) {
        return createReplaceCommand(ACCESS_TOKEN_PATCH, values);
    }

    public static OAuth2Command removeAccessTokenClaims(String... keys) {
        return createRemoveCommand(ACCESS_TOKEN_PATCH, keys);
    }

    private static OAuth2Command createAddCommand(String type, Map<String, Object> values) {
        return new OAuth2Command(type, values.entrySet().stream()
                .map(entry -> new PatchOperation("add", "/claims/" + entry.getKey(), entry.getValue()))
                .collect(Collectors.toList()));
    }

    private static OAuth2Command createRemoveCommand(String type, String... keys) {
        return new OAuth2Command(type, Arrays.stream(keys)
            .map(key -> new PatchOperation("remove", "/claims/" + key, null))
            .collect(Collectors.toList()));
    }

    private static OAuth2Command createExpirationCommand(String type, int expiration) {
        return new OAuth2Command(type, Collections.singletonList(
            new PatchOperation("replace", "/token/lifetime/expiration", expiration)));
    }

    private static OAuth2Command createReplaceCommand(String type, Map<String, Object> values) {
        return new OAuth2Command(type, values.entrySet().stream()
            .map(entry -> new PatchOperation("replace", "/claims/" + entry.getKey(), entry.getValue()))
            .collect(Collectors.toList()));
    }
}