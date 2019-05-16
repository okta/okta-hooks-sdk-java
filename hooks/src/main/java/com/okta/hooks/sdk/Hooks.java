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
package com.okta.hooks.sdk;

import com.okta.hooks.sdk.models.Command;
import com.okta.hooks.sdk.models.HookError;
import com.okta.hooks.sdk.models.HookErrorCause;
import com.okta.hooks.sdk.models.HookResponse;
import com.okta.hooks.sdk.models.OAuth2Command;
import com.okta.hooks.sdk.models.UserRegistrationCommand;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

public class Hooks {

    private Hooks() {}

    public static Builder builder() {
        return new DefaultBuilder();
    }

    public static HookResponse noop() {
        return new HookResponse();
    }

    public interface Builder {

        Builder error(String message);

        Builder oauth2(OAuth2Command... commands);

        Builder userRegistration(UserRegistrationCommand... commands);

        Builder debugContext(Map<String, Object> data);

        HookResponse build();
    }

    private static class DefaultBuilder implements Builder {

        private List<Command> commands = new ArrayList<>();
        private Map<String, Object> debugContext;
        private HookError error;

        @Override
        public Builder error(String message) {
            this.error = new HookError()
                    .setErrorSummary(message + "-summary")
                    .setErrorCauses(Arrays.asList(
                    new HookErrorCause()
                        .setErrorSummary(message)
            ));
            return this;
        }

        @Override
        public Builder oauth2(OAuth2Command... commands) {
            // TODO assert not null, maybe check if list already has commands?
            this.commands.addAll(Arrays.asList(commands));
            return this;
        }

        @Override
        public Builder userRegistration(UserRegistrationCommand... commands) {
            // TODO assert not null, maybe check if list already has commands?
            this.commands.addAll(Arrays.asList(commands));
            return this;
        }

        @Override
        public Builder debugContext(Map<String, Object> data) {
            this.debugContext = data;
            return this;
        }

        @Override
        public HookResponse build() {
            return new HookResponse()
                    .setError(error)
                    .setCommands(commands)
                    .setDebugContext(debugContext);
        }
    }
}