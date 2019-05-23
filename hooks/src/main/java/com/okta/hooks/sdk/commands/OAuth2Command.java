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
        return createAddCommand("com.okta.identity.patch", values);
    }

    public static  OAuth2Command addAccessTokenClaim(String key, Object value) {
        return addAccessTokenClaims(Collections.singletonMap(key, value));
    }

    public static  OAuth2Command addAccessTokenClaims(Map<String, Object> values) {
        return createAddCommand("com.okta.access.patch", values);
    }

    private static OAuth2Command createAddCommand(String type, Map<String, Object> values) {
        return new OAuth2Command(type, values.entrySet().stream()
                .map(entry -> new PatchOperation("add", "/claims/" + entry.getKey(), entry.getValue()))
                .collect(Collectors.toList()));
    }
}