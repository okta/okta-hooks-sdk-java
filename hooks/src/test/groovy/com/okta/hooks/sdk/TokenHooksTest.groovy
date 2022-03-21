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
import static com.okta.hooks.sdk.commands.OAuth2Command.addAccessTokenClaim
import static com.okta.hooks.sdk.commands.OAuth2Command.addIdTokenClaim
import static com.okta.hooks.sdk.commands.OAuth2Command.addIdTokenClaims
import static com.okta.hooks.sdk.commands.OAuth2Command.removeAccessTokenClaims
import static com.okta.hooks.sdk.commands.OAuth2Command.removeIdTokenClaims
import static com.okta.hooks.sdk.commands.OAuth2Command.replaceAccessTokenClaims
import static com.okta.hooks.sdk.commands.OAuth2Command.replaceIdTokenClaims
import static com.okta.hooks.sdk.commands.OAuth2Command.setAccessTokenExpiration
import static com.okta.hooks.sdk.commands.OAuth2Command.setIdTokenExpiration
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
    void addClaim() {

        def builder = Hooks.builder()
            .oauth2(
                addAccessTokenClaim("access1", "value1"),
                addIdTokenClaim("id1", "value1")
            )

        def expectedToString = expected """
        {"commands": [
            { "type": "com.okta.access.patch",
              "value": [
                { "op": "add",
                  "path": "/claims/access1",
                  "value": "value1"
                }
              ]
            },
            { "type": "com.okta.identity.patch",
              "value": [
                { "op": "add",
                  "path": "/claims/id1",
                  "value": "value1"
                }
              ]
            }
          ]
        }
        """
        assertJsonEqualsNonStrict builder.toString(), expectedToString
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
        assertJsonEqualsNonStrict builder.toString(), expectedToString
    }

    @Test
    void removeClaims() {

        def builder = Hooks.builder()
            .oauth2(
                removeAccessTokenClaims("access1", "access2", "access3"),
                removeIdTokenClaims("id1", "id2", "id3"))

        def expectedToString = expected """
        {"commands": [
            { "type": "com.okta.access.patch",
              "value": [
                { "op": "remove",
                  "path": "/claims/access1"
                },
                { "op": "remove",
                  "path": "/claims/access2"
                },
                { "op": "remove",
                  "path": "/claims/access3"
                }
              ]
            },
            { "type": "com.okta.identity.patch",
              "value": [
                { "op": "remove",
                  "path": "/claims/id1"
                },
                { "op": "remove",
                  "path": "/claims/id2"
                },
                { "op": "remove",
                  "path": "/claims/id3"
                }
              ]
            }
          ]
        }
        """
        assertJsonEqualsNonStrict builder.toString(), expectedToString
    }

    @Test
    void replaceClaims() {

        def builder = Hooks.builder()
            .oauth2(replaceAccessTokenClaims([
                access1:    "value1",
                access2:    2,
                access3:    true,
                access4:    ["one", "two", "three"]]),
                replaceIdTokenClaims([
                    id1:    "value1",
                    id2:    2,
                    id3:    true,
                    id4:    ["one", "two", "three"]]))

        def expectedToString = expected """
        {"commands": [
            { "type": "com.okta.access.patch",
              "value": [
                { "op": "replace",
                  "path": "/claims/access1",
                  "value": "value1"
                },
                { "op": "replace",
                  "path": "/claims/access2",
                  "value": 2
                },
                { "op": "replace",
                  "path": "/claims/access3",
                  "value": true
                },
                { "op": "replace",
                  "path": "/claims/access4",
                  "value": ["one", "two", "three"]
                }
              ]
            },
            { "type": "com.okta.identity.patch",
              "value": [
                { "op": "replace",
                  "path": "/claims/id1",
                  "value": "value1"
                },
                { "op": "replace",
                  "path": "/claims/id2",
                  "value": 2
                },
                { "op": "replace",
                  "path": "/claims/id3",
                  "value": true
                },
                { "op": "replace",
                  "path": "/claims/id4",
                  "value": ["one", "two", "three"]
                }
              ]
            }
          ]
        }
        """
        assertJsonEqualsNonStrict builder.toString(), expectedToString
    }

    @Test
    void tokenExpiration() {

        def builder = Hooks.builder()
            .oauth2(
                setAccessTokenExpiration(4242),
                setIdTokenExpiration(2424))

        def expectedToString = expected """
        {"commands": [
            { "type": "com.okta.access.patch",
              "value": [
                { "op": "replace",
                  "path": "/token/lifetime/expiration",
                  "value": 4242
                }
              ]
            },
            { "type": "com.okta.identity.patch",
              "value": [
                { "op": "replace",
                  "path": "/token/lifetime/expiration",
                  "value": 2424
                }
              ]
            }
          ]
        }
        """
        assertJsonEqualsNonStrict builder.toString(), expectedToString
    }
}
