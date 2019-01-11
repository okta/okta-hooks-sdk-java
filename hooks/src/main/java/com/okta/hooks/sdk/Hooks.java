package com.okta.hooks.sdk;

import com.okta.hooks.sdk.models.Command;
import com.okta.hooks.sdk.models.HookError;
import com.okta.hooks.sdk.models.HookErrorCause;
import com.okta.hooks.sdk.models.HookResponse;
import com.okta.hooks.sdk.models.OAuth2Command;
import com.okta.hooks.sdk.models.UserRegistrationCommand;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import static com.okta.hooks.sdk.models.OAuth2Command.addIdTokenClaim;
import static com.okta.hooks.sdk.models.OAuth2Command.addAccessTokenClaim;
import static com.okta.hooks.sdk.models.UserRegistrationCommand.denyRegistration;

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
            this.error = new HookError().setErrorCauses(Arrays.asList(
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

    public static void main(String[] args) {
        HookResponse example1 = Hooks.builder()
                .oauth2(addIdTokenClaim("myClaimKey", "my super cool value"),
                        addAccessTokenClaim("hello", Arrays.asList("a", "list", "value")))
                .debugContext(Collections.emptyMap()) // some map value here
                .build();

        HookResponse example2 = Hooks.builder()
                .userRegistration(denyRegistration("You shall not pass!"))
                .error("well, this is an error!")
                .build();
    }
}
