package com.okta.hooks.examples.spring;

import com.okta.hooks.sdk.Hooks;
import com.okta.hooks.sdk.commands.SamlAssertionCommand;
import com.okta.hooks.sdk.commands.UserImportCommand;
import com.okta.hooks.sdk.commands.UserRegistrationCommand;

import java.util.Collections;

import static com.okta.hooks.sdk.commands.OAuth2Command.addAccessTokenClaim;
import static com.okta.hooks.sdk.commands.OAuth2Command.addIdTokenClaim;
import static com.okta.hooks.sdk.commands.SamlAssertionCommand.add;
import static com.okta.hooks.sdk.commands.SamlAssertionCommand.replace;
import static com.okta.hooks.sdk.commands.UserImportCommand.createUser;
import static com.okta.hooks.sdk.commands.UserImportCommand.linkUser;
import static com.okta.hooks.sdk.commands.UserRegistrationCommand.allowRegistration;
import static com.okta.hooks.sdk.commands.UserRegistrationCommand.denyRegistration;

/**
 * Code Snippets used in this projects README.
 */
@SuppressWarnings("unused")
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