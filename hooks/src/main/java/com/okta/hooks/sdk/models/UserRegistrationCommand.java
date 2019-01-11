package com.okta.hooks.sdk.models;

import java.util.Collections;
import java.util.Map;

public class UserRegistrationCommand extends Command {

    private UserRegistrationCommand(String type, Object value) {
        super(type, value);
    }

    public static UserRegistrationCommand denyRegistration(String error) {
        // TODO handle error string
        return new UserRegistrationCommand("com.okta.action.update", Collections.singletonMap("registration", "DENY"));
    }

    public static UserRegistrationCommand denyRegistration() {
        return denyRegistration(null);
    }

    public static UserRegistrationCommand addProfileProperties(Map<String, Object> properties) {
        return new UserRegistrationCommand("com.okta.profile.update", properties);
    }


}