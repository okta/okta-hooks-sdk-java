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
package com.okta.hooks.examples.spring;

import com.okta.hooks.sdk.Hooks;
import com.okta.hooks.sdk.commands.SamlAssertionCommand;
import com.okta.hooks.sdk.commands.UserImportCommand;
import com.okta.hooks.sdk.commands.UserRegistrationCommand;

import java.util.Collections;

import static com.okta.hooks.sdk.commands.OAuth2Command.addAccessTokenClaim;
import static com.okta.hooks.sdk.commands.OAuth2Command.addIdTokenClaim;
import static com.okta.hooks.sdk.commands.OAuth2Command.replaceAccessTokenClaim;
import static com.okta.hooks.sdk.commands.OAuth2Command.setAccessTokenExpiration;
import static com.okta.hooks.sdk.commands.OAuth2Command.setIdTokenExpiration;
import static com.okta.hooks.sdk.commands.PasswordImportCommand.unverified;
import static com.okta.hooks.sdk.commands.PasswordImportCommand.verified;
import static com.okta.hooks.sdk.commands.SamlAssertionCommand.add;
import static com.okta.hooks.sdk.commands.SamlAssertionCommand.replace;
import static com.okta.hooks.sdk.commands.UserImportCommand.createUser;
import static com.okta.hooks.sdk.commands.UserImportCommand.linkUser;
import static com.okta.hooks.sdk.commands.UserRegistrationCommand.allowRegistration;
import static com.okta.hooks.sdk.commands.UserRegistrationCommand.denyRegistration;

/**
 * Code Snippets used in this projects README.
 */
@SuppressWarnings({"unused", "PMD.TooManyStaticImports"})
final class ReadmeSnippets {

    private void serializeToString() {
        String result = Hooks.builder()
                    .userRegistration(denyRegistration())
                    .toString();
    }

    private void error() {
        Hooks.builder()
            .error("Some Error")
            .build();
    }

    private void noop() {
        Hooks.builder()
            .build();
    }

    private void oAuthAddAccessTokenClaim() {
        Hooks.builder()
            .oauth2(addAccessTokenClaim("aClaim", "test-value"))
            .build();
    }

    private void oAuthAddIdTokenClaim() {
        Hooks.builder()
            .oauth2(addIdTokenClaim("iClaim", "another-value"))
            .build();

    }

    private void errorCause() {
        Hooks.builder()
            .errorCause("An Error")
            .build();
    }

    private void userRegDenyRegistration() {
        Hooks.builder()
            .userRegistration(denyRegistration())
            .build();
    }

    private void userRegAllowRegistration() {
        Hooks.builder()
            .userRegistration(allowRegistration())
            .build();
    }

    private void userRegProfileProperty() {
        Hooks.builder()
            .userRegistration(UserRegistrationCommand.addProfileProperties(
                    Collections.singletonMap("someKey", "a-value")))
            .build();
    }

    private void userImportProfileProperty() {
        Hooks.builder()
            .userImport(UserImportCommand.addProfileProperties(
                    Collections.singletonMap("someKey", "a-value")))
            .build();
    }

    private void userImportCreateUser() {
        Hooks.builder()
            .userImport(createUser())
            .build();
    }

    private void userImportLinkUser() {
        Hooks.builder()
            .userImport(linkUser("oktaUserId"))
            .build();
    }

    private void samlReplaceAssertion() {
        Hooks.builder()
            .samlAssertion(replace("/claims/array/attributeValues/1/value", "replacementValue"))
            .build();
    }

    private void passwordImportVerified() {
        Hooks.builder()
            .passwordImport(verified())
            .build();
    }

    private void passwordImportUnverified() {
        Hooks.builder()
            .passwordImport(unverified())
            .build();
    }

    private void accessTokenExpiration() {
        Hooks.builder()
            .oauth2(setAccessTokenExpiration(3600))
            .build();
    }

    private void idTokenExpiration() {
        Hooks.builder()
            .oauth2(setIdTokenExpiration(3600))
            .build();
    }

    private void replaceClaim() {
        Hooks.builder()
            .oauth2(replaceAccessTokenClaim("a-claim-name", "a-new-value"))
            .build();
    }

    private void samlAddAssertion() {
        Hooks.builder()
            .samlAssertion(add("/claims/foo", new SamlAssertionCommand.SamlAttribute()
                .setAttributes(Collections.singletonMap("NameFormat", "urn:oasis:names:tc:SAML:2.0:attrname-format:basic"))
                .setAttributeValues(Collections.singletonList(
                    new SamlAssertionCommand.SamlAttributeValue()
                        .setAttributes(Collections.singletonMap("xsi:type", "xs:string"))
                        .setValue("bearer")))))
            .build();
    }

    private void debugInfo() {
        Hooks.builder()
            .errorCause("An Error")
            .debugContext(Collections.singletonMap("key", "value"))
            .build();
    }
}