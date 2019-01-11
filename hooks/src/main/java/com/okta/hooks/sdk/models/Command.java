package com.okta.hooks.sdk.models;

import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
public class Command {

    private final String type;
    private final Object value;

}