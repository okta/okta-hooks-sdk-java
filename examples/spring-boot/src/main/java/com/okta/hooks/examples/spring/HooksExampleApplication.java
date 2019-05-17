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
package com.okta.hooks.examples.spring;

import com.okta.hooks.sdk.Hooks;
import com.okta.hooks.sdk.models.HookResponse;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.util.Arrays;

import static com.okta.hooks.sdk.models.OAuth2Command.addAccessTokenClaim;
import static com.okta.hooks.sdk.models.OAuth2Command.addIdTokenClaim;
import static com.okta.hooks.sdk.models.UserRegistrationCommand.denyRegistration;

@RestController
@SpringBootApplication
public class HooksExampleApplication {

    public static void main(String[] args) {
        SpringApplication.run(HooksExampleApplication.class, args);
    }

    @PostMapping("oauth2")
    public  HookResponse oauth2(@RequestBody String request) throws IOException {

        return Hooks.builder()
                .oauth2(addIdTokenClaim("myClaimKey", "my super cool value"),
                        addAccessTokenClaim("hello", Arrays.asList("a", "list", "value")))
                .build();
    }

    @PostMapping("user-reg")
    public HookResponse userReg(@RequestBody String request) throws IOException {

        return Hooks.builder()
                .userRegistration(denyRegistration())
                .build();
    }
}
