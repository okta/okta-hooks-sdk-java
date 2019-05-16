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
package com.okta.hooks.sdk.its.app;

import java.util.LinkedHashMap;
import java.util.Map;

public class BaseController {

    private final RequestLog requestLog;
    private final String baseUrl;

    public BaseController(String baseUrl, RequestLog requestLog) {
        this.baseUrl = baseUrl;
        this.requestLog = requestLog;
    }

    void log(String path, Map<String, Object> args) {
        requestLog.addEntry(baseUrl + "/" +path, args);
    }

    void log(String path, String key1, Object value1) {
        Map<String, Object> args = new LinkedHashMap<>();
        args.put(key1, value1);
        log(path, args);
    }
}
