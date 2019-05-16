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
package com.okta.hooks.sdk.models;

import java.util.Collections;
import java.util.Map;

public class UserRegistrationCommand extends Command {

    private UserRegistrationCommand(String type, Object value) {
        super(type, value);
    }

    public static UserRegistrationCommand denyRegistration() {
        return registrationUpdate("DENY");
    }

    public static UserRegistrationCommand allowRegistration() {
        return registrationUpdate("ALLOW");
    }

    private static UserRegistrationCommand registrationUpdate(String denyOrAllow) {
        return new UserRegistrationCommand("com.okta.action.update", Collections.singletonMap("registration", denyOrAllow));
    }

    public static UserRegistrationCommand addProfileProperties(Map<String, Object> properties) {
        return new UserRegistrationCommand("com.okta.user.profile.update", properties);
    }


}