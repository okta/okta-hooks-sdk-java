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
package com.okta.hooks.sdk.jackson;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.auto.service.AutoService;
import com.okta.hooks.sdk.HookResponseSerializer;
import com.okta.hooks.sdk.SerializationException;
import com.okta.hooks.sdk.commands.HookResponse;

@AutoService(HookResponseSerializer.class)
public class JacksonSerializer implements HookResponseSerializer {

    private final ObjectMapper objectMapper;

    public JacksonSerializer() {
        this.objectMapper = new ObjectMapper()
                .setSerializationInclusion(JsonInclude.Include.NON_NULL);
    }

    @Override
    public String serialize(HookResponse hookResponse) throws SerializationException {
        try {
            return objectMapper.writeValueAsString(hookResponse);
        } catch (JsonProcessingException e) {
            throw new SerializationException("failed to serialize hook response", e);
        }
    }
}