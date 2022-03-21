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

import com.okta.hooks.sdk.commands.HookErrorCause
import org.testng.annotations.Test

import static com.okta.hooks.sdk.commands.UserRegistrationCommand.addProfileProperties
import static com.okta.hooks.sdk.commands.UserRegistrationCommand.allowRegistration
import static com.okta.hooks.sdk.commands.UserRegistrationCommand.denyRegistration
import static org.hamcrest.MatcherAssert.assertThat
import static org.hamcrest.Matchers.is

class UserRegHooksTest implements HooksSupport {

    @Test
    void singleErrorCause() {

        def builder = Hooks.builder()
            .errorCause("test-error")

        def expectedToString = expected"""{"error": {"errorCauses": [{ "errorSummary": "test-error" }]}}"""
        assertThat builder.toString(), is(expectedToString)
    }

    @Test
    void errorWithDebugContext() {

        def builder = Hooks.builder()
            .errorCause("test-error")
            .debugContext(["foo": "bar", "one": "two"])

        def expectedToString = expected"""{"error": {"errorCauses": [{ "errorSummary": "test-error" }]}, "debugContext": {"foo": "bar", "one": "two"}}"""
        assertJsonEqualsNonStrict builder.toString(), expectedToString
    }

    @Test
    void complexError() {

        def builder = Hooks.builder()
            .error("Errors were found in the user profile")
            .errorCause(new HookErrorCause()
                .setErrorSummary("You specified an invalid email domain")
                .setReason("INVALID_EMAIL_DOMAIN")
                .setLocationType("body")
                .setLocation("data.userProfile.login")
                .setDomain("end-user"))
            .errorCause(new HookErrorCause()
                .setErrorSummary("You failed this test")
                .setReason("EXPECTED_TEST_ERROR")
                .setLocationType("arm")
                .setLocation("data.fake.location")
                .setDomain("foobar"))

        def expectedToString = expected """
        {
           "error":{
              "errorSummary":"Errors were found in the user profile",
              "errorCauses":[
                 { "errorSummary":"You specified an invalid email domain",
                    "reason":"INVALID_EMAIL_DOMAIN",
                    "locationType":"body",
                    "location":"data.userProfile.login",
                    "domain":"end-user"
                 },
                 { "errorSummary":"You failed this test",
                    "reason":"EXPECTED_TEST_ERROR",
                    "locationType":"arm",
                    "location":"data.fake.location",
                    "domain":"foobar"
                 }
              ]
           }
        }
        """
        assertJsonEqualsNonStrict builder.toString(), expectedToString
    }

    @Test
    void multipleErrorCause() {

        def builder = Hooks.builder()
            .errorCause("test-error1")
            .errorCause("test-error2")

        def expectedToString = expected"""{"error": {"errorCauses": [{ "errorSummary": "test-error1" },{ "errorSummary": "test-error2" }]}}"""
        assertJsonEqualsNonStrict builder.toString(), expectedToString
    }

    @Test
    void deny() {

        def builder = Hooks.builder()
            .userRegistration(denyRegistration())

        def expectedToString = expected """
        { "commands":[
              { "type":"com.okta.action.update",
                 "value":{
                    "registration":"DENY"
                 }
              }
           ]
        }
        """

        assertJsonEqualsNonStrict builder.toString(), expectedToString
    }

    @Test
    void allow() {

        def builder = Hooks.builder()
            .userRegistration(allowRegistration())

        def expectedToString = expected """
        { "commands":[
              { "type":"com.okta.action.update",
                 "value":{
                    "registration":"ALLOW"
                 }
              }
           ]
        }
        """

        assertJsonEqualsNonStrict builder.toString(), expectedToString
    }

    @Test
    void updateProfile() {
        def builder = Hooks.builder()
            .userRegistration(addProfileProperties([
                                prop1:    "value1",
                                prop2:    2,
                                prop3:    true,
                                prop4:    ["one", "two", "three"]]))

        def expectedToString = expected """
        { "commands":[
              { "type":"com.okta.user.profile.update",
                 "value":{
                    "prop1":"value1",
                    "prop2": 2,
                    "prop3": true,
                    "prop4": ["one", "two", "three"]
                 }
              }
           ]
        }
        """

        assertJsonEqualsNonStrict builder.toString(), expectedToString
    }
}