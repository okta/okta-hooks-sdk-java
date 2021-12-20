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

import com.okta.hooks.sdk.commands.Command;
import com.okta.hooks.sdk.commands.HookError;
import com.okta.hooks.sdk.commands.HookErrorCause;
import com.okta.hooks.sdk.commands.HookResponse;
import com.okta.hooks.sdk.commands.OAuth2Command;
import com.okta.hooks.sdk.commands.PasswordImportCommand;
import com.okta.hooks.sdk.commands.SamlAssertionCommand;
import com.okta.hooks.sdk.commands.UserImportCommand;
import com.okta.hooks.sdk.commands.UserRegistrationCommand;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.ServiceLoader;

public class Hooks {

    private static final HookResponseSerializer SERIALIZER = loadSerializer();

    private Hooks() {}

    public static Builder builder() {
        return new DefaultBuilder();
    }

    public static HookResponse noop() {
        return new HookResponse();
    }

    public interface Builder {

        Builder error(String message);

        Builder errorCause(String message);

        Builder errorCause(HookErrorCause cause);

        Builder oauth2(OAuth2Command... commands);

        Builder userRegistration(UserRegistrationCommand... commands);

        Builder userImport(UserImportCommand... commands);

        Builder passwordImport(PasswordImportCommand... commands);

        Builder samlAssertion(SamlAssertionCommand... commands);

        Builder debugContext(Map<String, Object> data);

        HookResponse build();
    }

    private static class DefaultBuilder implements Builder {

        private List<Command> commands;
        private Map<String, Object> debugContext;
        private HookError error;

        @Override
        public Builder error(String message) {
            this.error = getOrCreateError(false, message)
                    .setErrorSummary(message);
            return this;
        }

        @Override
        public Builder errorCause(String message) {
            return errorCause(new HookErrorCause()
                    .setErrorSummary(message));
        }

        @Override
        public Builder errorCause(HookErrorCause cause) {
            error = getOrCreateError(true, cause.getErrorSummary());
            error.getErrorCauses().add(cause);
            return this;
        }

        @Override
        public Builder oauth2(OAuth2Command... commands) {
            return addCommands(commands);
        }

        @Override
        public Builder userRegistration(UserRegistrationCommand... commands) {
            return addCommands(commands);
        }

        @Override
        public Builder userImport(UserImportCommand... commands) {
            return addCommands(commands);
        }

        @Override
        public Builder passwordImport(PasswordImportCommand... commands) {
            return addCommands(commands);
        }

        @Override
        public Builder samlAssertion(SamlAssertionCommand... commands) {
            return addCommands(commands);
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

        @Override
        public String toString() {
            return SERIALIZER.serialize(build());
        }

        private HookError getOrCreateError(boolean initCauses, String errorSummary) {

            HookError hookError = error != null ? error : new HookError().setErrorSummary(errorSummary);

            if (initCauses && hookError.getErrorCauses() == null) {
                hookError.setErrorCauses(new ArrayList<>());
            }
            return hookError;
        }

        private Builder addCommands(Command... commands) {
            ensureCommands().addAll(Arrays.asList(commands));
            return this;
        }

        private List<Command> ensureCommands() {
            if (commands == null) {
                commands = new ArrayList<>();
            }
            return commands;
        }
    }

    private static HookResponseSerializer loadSerializer() {
        Iterator<HookResponseSerializer> iter = ServiceLoader.load(HookResponseSerializer.class).iterator();
        if (!iter.hasNext()) {
            throw new IllegalStateException("Could not load HookResponseSerializer from ServiceLoader");
        }
        return iter.next();
    }
}