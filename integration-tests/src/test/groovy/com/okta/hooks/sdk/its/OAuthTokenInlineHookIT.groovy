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
import com.okta.sdk.impl.resource.DefaultExtensibleResource
import com.okta.sdk.resource.Deletable
import com.okta.sdk.resource.ExtensibleResource
import com.okta.sdk.resource.application.*
import io.jsonwebtoken.Claims
import io.jsonwebtoken.JwsHeader
import io.jsonwebtoken.Jwts
import io.jsonwebtoken.SigningKeyResolver
import io.restassured.http.ContentType
import io.restassured.response.ExtractableResponse
import org.testng.annotations.AfterClass
import org.testng.annotations.Test

import java.security.Key

import static io.restassured.RestAssured.given
import static org.hamcrest.MatcherAssert.assertThat
import static org.hamcrest.Matchers.*

@Test(singleThreaded = true)
class OAuthTokenInlineHookIT extends ITSupport {

    private Application application
    private ExtensibleResource clientCredsResponse
    private ExtensibleResource authorizationServer
    private ExtensibleResource policyRule

    OAuthTokenInlineHookIT() {
        super("oauth-token", "com.okta.oauth2.tokens.transform")
    }

    @Test
    void happyPath() {

        int initialCount = applicationContext.getBean(RequestLog).size()

        // create hook
        def hookId = createHook("success")

        // configure everything
        configureEnvironmentForTokenHook(hookId)

        // create user
        def user = randomUser()

        // login
        login(user.getProfile().getLogin(), "Password1")

        // validate hook was called
        assertThat applicationContext.getBean(RequestLog).size(), greaterThan(initialCount)
    }

    @Test
    void webHookReturnsError() {

        int initialCount = applicationContext.getBean(RequestLog).size()

        // create hook
        def hookId = createHook("error")

        // configure everything
        configureEnvironmentForTokenHook(hookId)

        // create user
        def user = randomUser()

        // login
        login(user.getProfile().getLogin(), "Password1", 403)

        // validate hook was called
        assertThat applicationContext.getBean(RequestLog).size(), greaterThan(initialCount)
    }

    @Test
    void addAccessTokenClaim() {

        int initialCount = applicationContext.getBean(RequestLog).size()

        def hookId = createHook("add-access-claim")

        // configure everything
        configureEnvironmentForTokenHook(hookId)

        // create user
        def user = randomUser()

        // login
        def response = login(user.getProfile().getLogin(), "Password1")

        // validate hook was called
        assertThat applicationContext.getBean(RequestLog).size(), greaterThan(initialCount)

        Claims claims = getJwtClaims(response.jsonPath().getString("access_token"))
        assertThat(claims.get("aClaim", String), is("test-value"))
    }

    @Test
    void addIdTokenClaim() {

        int initialCount = applicationContext.getBean(RequestLog).size()

        def hookId = createHook("add-id-claim")

        // configure everything
        configureEnvironmentForTokenHook(hookId)

        // create user
        def user = randomUser()

        // login
        def response = login(user.getProfile().getLogin(), "Password1")

        // validate hook was called
        assertThat applicationContext.getBean(RequestLog).size(), greaterThan(initialCount)

        Claims claims = getJwtClaims(response.jsonPath().get("id_token"))
        assertThat(claims.get("iClaim", String), is("another-value"))
    }

    @Test
    void addBothTokenClaims() {

        int initialCount = applicationContext.getBean(RequestLog).size()

        def hookId = createHook("add-both-claim")

        // configure everything
        configureEnvironmentForTokenHook(hookId)

        // create user
        def user = randomUser()

        // login
        def response = login(user.getProfile().getLogin(), "Password1")

        // validate hook was called
        assertThat applicationContext.getBean(RequestLog).size(), greaterThan(initialCount)

        Claims accessTokenClaims = getJwtClaims(response.jsonPath().getString("access_token"))
        Claims idTokenClaims = getJwtClaims(response.jsonPath().get("id_token"))

        assertThat accessTokenClaims.get("access1"), is("value1")
        assertThat accessTokenClaims.get("access2"), is(2)
        assertThat accessTokenClaims.get("access3"), is(true)
        assertThat accessTokenClaims.get("access4"), is(["one", "two", "three"])

        assertThat idTokenClaims.get("id1"), is("value1")
        assertThat idTokenClaims.get("id2"), is(2)
        assertThat idTokenClaims.get("id3"), is(true)
        assertThat idTokenClaims.get("id4"), is(["one", "two", "three"])
    }

    ExtractableResponse login(String username,
                              String password,
                              int expectedStatus=200,
                              String clientId=clientCredsResponse.getString("client_id"),
                              String clientSecret=clientCredsResponse.getString("client_secret"),
                              String issuer=authorizationServer.issuer) {
        return given()
            .redirects()
                .follow(false)
            .accept(ContentType.JSON)
            .contentType("application/x-www-form-urlencoded")
            .auth()
                .preemptive()
                    .basic(clientId, clientSecret)
            .formParam("grant_type", "password")
            .formParam("username", username)
            .formParam("password", password)
            .formParam("scope", "openid profile email")
        .when()
            .post("${issuer}/v1/token")
        .then()
            .statusCode(expectedStatus)
            .extract()
    }

    Claims getJwtClaims(String token) {
        try {
            // a bit of a hack, but just parse the claims for testing
            return Jwts.parser()
                    .setSigningKeyResolver(new SigningKeyResolver() {
                        @Override
                        Key resolveSigningKey(JwsHeader header, Claims claims) {
                            throw new UglyClaimContainerException(claims)
                        }

                        @Override
                        Key resolveSigningKey(JwsHeader header, String plaintext) {
                            return null
                        }
                    })
                    .parseClaimsJws(token)
                    .getBody()
        } catch(UglyClaimContainerException e) {
            return e.getClaims()
        }
    }

    static class UglyClaimContainerException extends RuntimeException {
        private final Claims claims

        UglyClaimContainerException(Claims claims) {
            this.claims = claims
        }

        Claims getClaims() {
            return claims
        }
    }

    private void configureEnvironmentForTokenHook(String hookId) {

        if (policyRule == null) {

            // create app
            createApplication()

            // get credentials
            clientCredsResponse = getClient().http()
                    .get("/api/v1/internal/apps/${application.id}/settings/clientcreds", ExtensibleResource)

            // create authorization server
            createAuthorizationServerWithPolicy(hookId)
        } else {
            updatePolicyRule(hookId)
        }
    }

    Application createApplication() {

        // look up 'everyone' group id
        def everyoneGroupId = getClient().listGroups("everyone", null, null).single().getId()

        // create oauth application

        OpenIdConnectApplication app = client.instantiate(OpenIdConnectApplication)
                .setLabel("app-${randomUUID()}")
                .setSettings(client.instantiate(OpenIdConnectApplicationSettings)
                    .setOAuthClient(client.instantiate(OpenIdConnectApplicationSettingsClient)
                        .setRedirectUris(["https://example.com/oauth2/callback",
                                          "myapp://callback"])
                        .setResponseTypes([OAuthResponseType.TOKEN,
                                           OAuthResponseType.ID_TOKEN])
                        .setGrantTypes([OAuthGrantType.PASSWORD,
                                        OAuthGrantType.IMPLICIT])
                        .setApplicationType(OpenIdConnectApplicationType.NATIVE)))

        app = client.createApplication(app)

        ApplicationGroupAssignment aga = client.instantiate(ApplicationGroupAssignment)
                                            .setPriority(2) // TODO why prio?
        app.createApplicationGroupAssignment(everyoneGroupId, aga)

        this.application = app

        return app
    }

    ExtensibleResource createAuthorizationServerWithPolicy(String hookId, String appId = application.getId()) {
        def authorizationServerRequest = getClient().instantiate(ExtensibleResource)
        authorizationServerRequest.putAll([
              "name": "as-${randomUUID()}".toString(),
              "description": "test AS for Java Hooks SDK",
              "audiences": [
                "api://hooks"
              ]
        ])

        authorizationServer = getClient().http()
            .setBody(authorizationServerRequest)
            .post("/api/v1/authorizationServers", ExtensibleResource)

        // create a policy
        def policyRequest = getClient().instantiate(ExtensibleResource)
        policyRequest.putAll([
            conditions: [
                clients: [
                    include: [appId]
                ]
            ],
            description: "test-policy",
            name: "test-policy",
            type: "OAUTH_AUTHORIZATION_POLICY"

        ])

        ExtensibleResource policyResponse = getClient().http()
            .setBody(policyRequest)
            .post("/api/v1/authorizationServers/${authorizationServerRequest.id}/policies", ExtensibleResource)

        createPolicyRule(hookId, policyResponse.getString("id"))

        return authorizationServer
    }

    ExtensibleResource createPolicyRule(String hookId, String policyId, String authorizationServerId=authorizationServer.id) {
        def policyRuleRequest = getClient().instantiate(ExtensibleResource)
        policyRuleRequest.putAll([
                type: "RESOURCE_ACCESS",
                system: false,
                name: "test-rule",
                conditions: [
                    grantTypes: [
                        include: [
                            "client_credentials",
                            "authorization_code",
                            "implicit",
                            "password"
                        ]
                    ],
                    people: [
                        users: [
                            include: [],
                            exclude:[]
                        ],
                    groups: [
                        include: ["EVERYONE"],
                        exclude: []]
                    ],
                    scopes: [
                        include: ["*"]]
                ],
                actions: [
                    token: [
                        inlineHook: hookId,
                        accessTokenLifetimeMinutes: 60,
                        refreshTokenLifetimeMinutes: 0,
                        refreshTokenWindowMinutes: 10080
                    ]
                ]
        ])

        policyRule = getClient().http()
            .setBody(policyRuleRequest)
            .post("/api/v1/authorizationServers/${authorizationServerId}/policies/${policyId}/rules/", ExtensibleResource)
    }

    ExtensibleResource updatePolicyRule(String hookId) {
        assertThat "Policy Rule was expected to be created already", policyRule, notNullValue()

        policyRule.actions.token.inlineHook = hookId

        getClient().http()
            .setBody(policyRule)
            .put(policyRule.getResourceHref())

        return policyRule
    }

    @AfterClass
    void cleanupAppAndAuthServer() {
        application.deactivate()
        application.delete()
        toDeletable(authorizationServer).delete()
    }

    DeletableExtensibleResource toDeletable(ExtensibleResource delegate) {
        return new DeletableExtensibleResource(delegate)
    }

    static class DeletableExtensibleResource extends DefaultExtensibleResource implements Deletable {

        private final ExtensibleResource delegate

        DeletableExtensibleResource(ExtensibleResource delegate) {
            super(((DefaultExtensibleResource)delegate).getDataStore())
            this.delegate = delegate
        }

        @Override
        void delete() {
            getDataStore().delete(delegate)
        }
    }
}