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
package com.okta.hooks.sdk.its.app;

import com.okta.hooks.sdk.Hooks;
import com.okta.hooks.sdk.models.HookResponse;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import static com.okta.hooks.sdk.models.OAuth2Command.addAccessTokenClaim;
import static com.okta.hooks.sdk.models.OAuth2Command.addAccessTokenClaims;
import static com.okta.hooks.sdk.models.OAuth2Command.addIdTokenClaim;
import static com.okta.hooks.sdk.models.OAuth2Command.addIdTokenClaims;

@RestController
@RequestMapping("oauth-token/")
public class OAuth2HooksController extends BaseController {

    public OAuth2HooksController(RequestLog requestLog) {
        super("/oauth-token", requestLog);
    }

    @PostMapping("error")
    public HookResponse tokenError(@RequestBody String request) {

        log("error", "payload", request);

        return Hooks.builder()
                .error("expected test error")
                .build();
    }

    @PostMapping("success")
    public HookResponse success(@RequestBody String request) {

        log("success", "payload", request);

        return Hooks.builder()
                .build();
    }

    @PostMapping("add-access-claim")
    public HookResponse addAccessClaim(@RequestBody String request) {

        log("add-access-claim", "payload", request);

        return Hooks.builder()
                .oauth2(addAccessTokenClaim("aClaim", "test-value"))
                .build();
    }

    @PostMapping("add-id-claim")
    public HookResponse addIdClaim(@RequestBody String request) {

        log("add-id-claim", "payload", request);

        return Hooks.builder()
                .oauth2(addIdTokenClaim("iClaim", "another-value"))
                .build();
    }

    @PostMapping("add-both-claim")
    public HookResponse addBothClaim(@RequestBody String request) {

        log("add-both-claim", "payload", request);

        Map<String, Object> accessTokenClaims = new HashMap<>();
        accessTokenClaims.put("access1", "value1");
        accessTokenClaims.put("access2", 2);
        accessTokenClaims.put("access3", true);
        accessTokenClaims.put("access4", Arrays.asList("one", "two", "three"));

        Map<String, Object> idTokenClaims = new HashMap<>();
        idTokenClaims.put("id1", "value1");
        idTokenClaims.put("id2", 2);
        idTokenClaims.put("id3", true);
        idTokenClaims.put("id4", Arrays.asList("one", "two", "three"));

        return Hooks.builder()
                .oauth2(addAccessTokenClaims(accessTokenClaims),
                        addIdTokenClaims(idTokenClaims))
                .build();
    }
}