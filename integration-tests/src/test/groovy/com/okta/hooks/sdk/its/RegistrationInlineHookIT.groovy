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
package com.okta.hooks.sdk.its

import com.okta.hooks.sdk.its.app.RequestLog
import com.okta.sdk.resource.ExtensibleResource
import com.okta.sdk.resource.ResourceException
import org.testng.annotations.AfterClass
import org.testng.annotations.Test

import static org.hamcrest.MatcherAssert.assertThat
import static org.hamcrest.Matchers.greaterThan
import static org.hamcrest.Matchers.is

@Test(singleThreaded = true)
class RegistrationInlineHookIT extends ITSupport {

    private ExtensibleResource registrationPolicy

    RegistrationInlineHookIT() {
        super("user-reg", "com.okta.user.pre-registration")
    }

    @Test
    void userRegDenyTest() {

        int initialCount = applicationContext.getBean(RequestLog).size()

        String hookId = createHook("deny")

        // update reg policy
        createOrUpdateRegPolicy(hookId)

        expect(ResourceException, {registerRandomUser()})

        assertThat applicationContext.getBean(RequestLog).size(), greaterThan(initialCount)
    }

    @Test
    void userRegErrorTest() {

        int initialCount = applicationContext.getBean(RequestLog).size()

        String hookId = createHook("error")

        // update reg policy
        createOrUpdateRegPolicy(hookId)

        ResourceException e = expect(ResourceException, {registerRandomUser()})
        assertThat e.causes.get(0).getSummary(), is("expected test error")

        assertThat applicationContext.getBean(RequestLog).size(), greaterThan(initialCount)
    }

    @Test
    void userRegAllowTest() {

        int initialCount = applicationContext.getBean(RequestLog).size()

        String hookId = createHook("allow")

        createOrUpdateRegPolicy(hookId)

        registerRandomUser()

        assertThat applicationContext.getBean(RequestLog).size(), greaterThan(initialCount)
    }

    @Test
    void userRegAddProfilePropertiesTest() {

        ensureCustomProperties()

        int initialCount = applicationContext.getBean(RequestLog).size()

        String hookId = createHook("update-profile")

        createOrUpdateRegPolicy(hookId)

        def email = randomEmail()
        registerRandomUser(email)

        assertThat applicationContext.getBean(RequestLog).size(), greaterThan(initialCount)

        def user = getClient().getUser(email)
        assertThat user.getProfile().getString("viaHooksTest"), is("expected-test-value")
    }

    ExtensibleResource registerRandomUser(String email = randomEmail(), String regPolicyId = registrationPolicy.id) {

        ExtensibleResource regBody = getClient().instantiate(ExtensibleResource)
        regBody.putAll( [
                userProfile: [
                        email: email,
                        firstName: "Joe",
                        lastName: "Coder",
                        password: "Hunter1!"
                ],
                redirectUri: null,
                relayState: null
        ])

        return getClient().http()
                    .setBody(regBody)
                    .post("/api/v1/registration/${regPolicyId}/register", ExtensibleResource)
    }

    synchronized ExtensibleResource createOrUpdateRegPolicy(String hookId) {

        if (registrationPolicy == null) {
            registrationPolicy = createRegPolicy(hookId)
        } else {
            updateRegistrationHook(hookId)
        }
        return registrationPolicy
    }

    ExtensibleResource createRegPolicy(String hookId) {

        ExtensibleResource requestBody = getClient().instantiate(ExtensibleResource)
        requestBody.putAll( [
                profileSchema: [
                    properties: [
                        email: [
                            title: "Email - Title",
                            type: "string",
                            description: "Email",
                            defaultValue: "you@test.com"
                        ],
                        firstName: [
                            title: "First Name - Title",
                            type: "string",
                            description: "First Name"
                        ],
                        lastName: [
                            title: "Last Name - Title",
                            type: "string",
                            description: "Last Name"
                        ],
                        password: [
                            title: "Password - Title",
                            type: "string",
                            description: "Password",
                            defaultValue: "hunter2"
                        ]
                    ],
                    required: ["email", "password", "firstName", "lastName"],
                    fieldOrder: ["email", "password", "firstName", "lastName"]
            ],
            activateUser: true,
            inlineHookId: hookId,
            activationRequirements: [
                    emailVerificationRequiredToActivate: false
            ]
        ])

        return getClient().http()
            .setBody(requestBody)
            .post("/api/internal/v1/registration", ExtensibleResource)
    }

    void updateRegistrationHook(String hookId) {

        registrationPolicy.inlineHookId = hookId
        getClient().http()
            .setBody(registrationPolicy)
            .put("/api/internal/v1/registration/${registrationPolicy.id}")
    }

    private void ensureCustomProperties() {
        def userSchemaUri = "/api/v1/meta/schemas/user/default"

        ExtensibleResource userSchema = getClient().http().get(userSchemaUri, ExtensibleResource)
        Map customProperties = userSchema.get("definitions").get("custom").get("properties")

        boolean needsUpdate =
            ensureCustomProperty("viaHooksTest", [type: "string"], customProperties)

        if (needsUpdate)  {
            getClient().http()
                .setBody(userSchema)
                .post(userSchemaUri, ExtensibleResource)
        }
    }

    private static boolean ensureCustomProperty(String name, Map body, Map<String, Object> customProperties) {
        boolean addProperty = !customProperties.containsKey(name)
        if (addProperty) {
            body.putAll([
                title: name
            ])
            customProperties.put(name, body)
        }
        return addProperty
    }

    @AfterClass
    void cleanup() {

        if (registrationPolicy != null) {
            updateRegistrationHook(null)
        }
    }
}