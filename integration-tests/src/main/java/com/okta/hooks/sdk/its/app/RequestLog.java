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

import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Component
class RequestLog {

    private final List<LogEntry> entries = new ArrayList<>();

    void addEntry(LogEntry entry) {
        entries.add(entry);
    }

    void addEntry(String path, Map<String, Object> args) {
        addEntry(new LogEntry(path, args));
    }

    public List<LogEntry> getEntries() {
        return entries;
    }

    public int size() {
        return entries.size();
    }

    public static class LogEntry {

        private final String path;
        private final Map<String, Object> args;

        LogEntry(String path, Map<String, Object> args) {
            this.path = path;
            this.args = args;
        }

        public String getPath() {
            return path;
        }

        public Map<String, Object> getArgs() {
            return args;
        }
    }
}
