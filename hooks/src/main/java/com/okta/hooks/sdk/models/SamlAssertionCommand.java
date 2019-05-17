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
package com.okta.hooks.sdk.models;

import lombok.Data;
import lombok.experimental.Accessors;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

public class SamlAssertionCommand extends Command {

    private SamlAssertionCommand(String type, Object value) {
        super(type, value);
    }

    public static SamlAssertionCommand replace(String path, Object newValue) {
        return patch("replace", path, newValue);
    }

    public static SamlAssertionCommand add(String path, SamlAttribute newValue) {
        return patch("add", path, newValue);
    }

    private static SamlAssertionCommand patch(String op, String path, Object newValue) {
        return new SamlAssertionCommand("com.okta.assertion.patch", Collections.singleton(new PatchOperation(op, path, newValue)));
    }

    @Data
    @Accessors(chain = true)
    public static class SamlAttribute {

        private Map<String, String> attributes;
        private List<SamlAttributeValue> attributeValues;
    }

    @Data
    @Accessors(chain = true)
    public static class SamlAttributeValue {

        private Map<String, String> attributes;
        private String value;
    }
}