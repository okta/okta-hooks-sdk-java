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

public class UserImportCommand extends Command {

    private UserImportCommand(String type, Object value) {
        super(type, value);
    }

    public static UserImportCommand addProfileProperties(Map<String, Object> properties) {
        return new UserImportCommand("com.okta.user.profile.update", properties);
    }

    public static UserImportCommand createUser() {
        return updateAction("CREATE_USER");

    }

    public static UserImportCommand[] linkUser(String id) {
        return new UserImportCommand[] {
                linkUser(),
                updateUser(id)
        };
    }

    private static UserImportCommand linkUser() {
        return updateAction("LINK_USER");
    }

    private static UserImportCommand updateUser(String id) {
        return new UserImportCommand("com.okta.user.update", Collections.singletonMap("id", id));
    }

    private static UserImportCommand updateAction(String result) {
        return new UserImportCommand("com.okta.action.update", Collections.singletonMap("result", result));
    }
}
