package com.okta.hooks.sdk.models;

import lombok.Data;
import lombok.experimental.Accessors;

import java.util.Collections;
import java.util.List;

@Data
@Accessors(chain = true)
public class HookError {

//    private final String errorSummary;
    private List<HookErrorCause> errorCauses = Collections.emptyList();

}
