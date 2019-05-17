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

import static com.okta.hooks.sdk.models.UserImportCommand.createUser
import static com.okta.hooks.sdk.models.UserImportCommand.linkUser
import static org.hamcrest.MatcherAssert.assertThat
import static org.hamcrest.Matchers.is
import static com.okta.hooks.sdk.models.UserImportCommand.addProfileProperties

class ImportHooksTest implements HooksSupport {

    @Test
    void updateUserProfile() {

        def builder = Hooks.builder()
            .userImport(
                addProfileProperties([
                        firstName: "Stan"
                ])
        )

        def expectedToString = expected """
            { "commands": [{
                "type": "com.okta.user.profile.update",
                "value": {
                  "firstName": "Stan"
                }
              }]
            }
        """

        assertThat builder.toString(), is(expectedToString)
    }

    @Test
    void createUserTest() {

        def builder = Hooks.builder()
                .userImport(createUser())

        def expectedToString = expected """
        { "commands": [{
            "type": "com.okta.action.update",
            "value": {
              "result": "CREATE_USER"
            }
          }]
        }
        """

        assertThat builder.toString(), is(expectedToString)
    }

    @Test
    void linkUserTest() {

        def builder = Hooks.builder()
                .userImport(linkUser("00garwpuyxHaWOkdV0g4"))

        def expectedToString = expected """
        { "commands": [{
            "type": "com.okta.action.update",
            "value": {
              "result": "LINK_USER"
            }
          }, {
            "type": "com.okta.user.update",
            "value": {
              "id": "00garwpuyxHaWOkdV0g4"
            }
          }]
        }
        """

        assertThat builder.toString(), is(expectedToString)
    }
}