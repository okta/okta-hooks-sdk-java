package com.okta.hooks.sdk.models;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Data
@NoArgsConstructor
@Accessors(chain = true)
public class HookResponse {

    private HookError error;
    private List<Command> commands = new ArrayList<>();
    private Map<String, Object> debugContext;

}