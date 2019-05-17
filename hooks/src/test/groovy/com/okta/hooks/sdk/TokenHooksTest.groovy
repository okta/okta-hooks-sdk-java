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
package com.okta.hooks.sdk

import org.testng.annotations.Test

import static com.okta.hooks.sdk.commands.OAuth2Command.addAccessTokenClaims
import static com.okta.hooks.sdk.commands.OAuth2Command.addIdTokenClaims
import static org.hamcrest.MatcherAssert.assertThat
import static org.hamcrest.Matchers.is

class TokenHooksTest implements HooksSupport {

    @Test
    void basicError() {

        def builder = Hooks.builder()
            .error("test-error")

        def expectedToString = expected"""{"error": {"errorSummary": "test-error"}}"""
        assertThat builder.toString(), is(expectedToString)
    }

    @Test
    void emptySuccess() {

        def expectedToString = expected "{}"
        assertThat Hooks.builder().toString(), is(expectedToString)
    }

    @Test
    void addClaims() {

        def builder = Hooks.builder()
                .oauth2(addAccessTokenClaims([
                            access1:    "value1",
                            access2:    2,
                            access3:    true,
                            access4:    ["one", "two", "three"]]),
                        addIdTokenClaims([
                            id1:    "value1",
                            id2:    2,
                            id3:    true,
                            id4:    ["one", "two", "three"]]))

        def expectedToString = expected """
        {"commands": [
            { "type": "com.okta.access.patch",
              "value": [
                { "op": "add",
                  "path": "/claims/access1",
                  "value": "value1"
                },
                { "op": "add",
                  "path": "/claims/access2",
                  "value": 2
                },
                { "op": "add",
                  "path": "/claims/access3",
                  "value": true
                },
                { "op": "add",
                  "path": "/claims/access4",
                  "value": ["one", "two", "three"]
                }
              ]
            },
            { "type": "com.okta.identity.patch",
              "value": [
                { "op": "add",
                  "path": "/claims/id1",
                  "value": "value1"
                },
                { "op": "add",
                  "path": "/claims/id2",
                  "value": 2
                },
                { "op": "add",
                  "path": "/claims/id3",
                  "value": true
                },
                { "op": "add",
                  "path": "/claims/id4",
                  "value": ["one", "two", "three"]
                }
              ]
            }
          ]
        }
        """
        assertThat builder.toString(), is(expectedToString)
    }
}
