package com.okta.hooks.sdk.models;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

@Data
@NoArgsConstructor
@Accessors(chain = true)
public class HookErrorCause {
    private String errorSummary;
//    private String reason;
//    private String  locationType;
//    private String location;
//    private String domain;
}
