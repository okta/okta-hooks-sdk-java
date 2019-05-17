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

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import static com.okta.hooks.sdk.models.UserRegistrationCommand.addProfileProperties;
import static com.okta.hooks.sdk.models.UserRegistrationCommand.allowRegistration;
import static com.okta.hooks.sdk.models.UserRegistrationCommand.denyRegistration;

@RestController
@RequestMapping("user-reg/")
public class UserRegistrationController extends BaseController {

    public UserRegistrationController(RequestLog requestLog) {
        super("/user-reg", requestLog);
    }

    @PostMapping("error")
    public HookResponse userRegError(@RequestBody String request) {

        log("error", "payload", request);

        return Hooks.builder()
                .errorCause("expected test error")
                .build();
    }

    @PostMapping("deny")
    public HookResponse userRegDeny(@RequestBody String request) {

        log("deny", "payload", request);

        return Hooks.builder()
                .userRegistration(denyRegistration())
                .build();
    }

    @PostMapping("allow")
    public HookResponse userRegAllow(@RequestBody String request) {

        log("allow", "payload", request);

        return Hooks.builder()
                .userRegistration(allowRegistration())
                .build();
    }

    @PostMapping("update-profile")
    public HookResponse userRegUpdateProfile(@RequestBody String request) {

        log("update-profile", "payload", request);

        return Hooks.builder()
                .userRegistration(addProfileProperties(
                        Collections.singletonMap("viaHooksTest", "expected-test-value")))
                .build();
    }
}
