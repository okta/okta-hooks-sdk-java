package com.okta.hooks.sdk.models;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
class PatchOperation {

    private final String op;
    private final String path;
    private final Object value;
}
