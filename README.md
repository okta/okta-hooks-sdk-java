[<img src="https://devforum.okta.com/uploads/oktadev/original/1X/bf54a16b5fda189e4ad2706fb57cbb7a1e5b8deb.png" align="right" width="256px"/>](https://devforum.okta.com/)
<!-- [![Maven Central](https://img.shields.io/maven-central/v/com.okta.hooks/okta-hooks-sdk-root.svg)](https://search.maven.org/#search%7Cga%7C1%7Cg%3A%22com.okta.commons%22%20a%3A%22okta-commons-root%22) -->
[![License](https://img.shields.io/badge/License-Apache%202.0-blue.svg)](https://opensource.org/licenses/Apache-2.0)
[![Support](https://img.shields.io/badge/support-Developer%20Forum-blue.svg)][devforum]
<!-- [![API Reference](https://img.shields.io/badge/docs-reference-lightgrey.svg)][javadocs] -->

Okta Hooks SDK for Java
=================

* [Release status](#release-status)
* [Need help?](#need-help)
* [Getting started](#getting-started)
* [Usage guide](#usage-guide)
* [Building the SDK](#building-the-sdk)
* [Contributing](#contributing)

The Okta Hooks SDK for Java project contains utilities to make responding to Okta's Hooks easy.
For more information on Okta's Inline hooks checkout out the [Documentation](https://developer.okta.com/docs/api/resources/inline-hooks/).

## Release status

This library uses semantic versioning and follows Okta's [library version policy](https://developer.okta.com/code/library-versions/).

:heavy_check_mark: The current beta major version series is: 0.1.x

| Version | Status                    |
| ------- | ------------------------- |
| 0.1.x   | :warning: Beta            |
 
The latest release can always be found on the [releases page][github-releases].

## Need help?
 
If you run into problems using the SDK, you can
 
* Ask questions on the [Okta Developer Forums][devforum]
* Post [issues][github-issues] here on GitHub (for code errors)

## Getting started
 
To use this SDK you will need to include the following dependencies:

For Apache Maven:

``` xml
<dependency>
    <groupId>com.okta.hooks</groupId>
    <artifactId>okta-hooks</artifactId>
    <version>${okta.version}</version>
</dependency>
```

For Gradle:

```groovy
compile "com.okta.hooks:okta-hooks:${okta.version}"
```

### SNAPSHOT Dependencies

Snapshots are deployed off of the 'master' branch to [OSSRH](https://oss.sonatype.org/) and can be consumed using the following repository configured for Apache Maven or Gradle:

```txt
https://oss.sonatype.org/content/repositories/snapshots/
```

## Usage guide

These examples will help you understand how to use this library. You can also browse the full [API reference documentation][javadocs].

This library currently supports building response objects which your application can send back Okta.

Serialization can be handled within the library or through another framework.

For example a simple Spring controller might look like:

```java
@PostMapping("user-reg")
public HookResponse userReg(@RequestBody String request) throws IOException {

    return Hooks.builder()
            .userRegistration(denyRegistration())
            .build();
}
```

Or serialize you could serialize directly, but calling the `toString()` method on the builder instance:

```java
String result = Hooks.builder()
            .userRegistration(denyRegistration())
            .toString();
```

### OAuth2/OIDC Tokens Hooks

Okta's [Token Inline Hook](https://developer.okta.com/use_cases/inline_hooks/token_hook/token_hook) docs

#### Error

```java
Hooks.builder()
    .error("Some Error")
    .build();
```

#### Noop Success

```java
Hooks.builder()
    .build();
```

#### Add Claim to Access Token

```java
Hooks.builder()
    .oauth2(addAccessTokenClaim("aClaim", "test-value"))
    .build();
```

#### Add Claim to ID Token

```java
Hooks.builder()
    .oauth2(addIdTokenClaim("iClaim", "another-value"))
    .build();
```

### User Registration Hooks

Okta's [Registration Inline Hook](https://developer.okta.com/use_cases/inline_hooks/registration_hook/registration_hook) docs

#### Error

```java
Hooks.builder()
    .errorCause("An Error")
    .build();
```

#### Deny Registration

```java
Hooks.builder()
    .userRegistration(denyRegistration())
    .build();
```

#### Allow Registration

```java
Hooks.builder()
    .userRegistration(allowRegistration())
    .build();
```

#### Add User Profile Property

```java
Hooks.builder()
    .userRegistration(addProfileProperties(
            Collections.singletonMap("someKey", "a-value")))
    .build();
```

### Import Users Hook

Okta's [Import Inline Hooks](https://developer.okta.com/use_cases/inline_hooks/import_hook/import_hook) docs

#### Add User Profile Property

```java
Hooks.builder()
    .userImport(addProfileProperties(
            Collections.singletonMap("someKey", "a-value")))
    .build();
```

#### Create User

```java
Hooks.builder()
    .userImport(createUser())
    .build();
```

#### Link User

```java
Hooks.builder()
    .userImport(linkUser("oktaUserId")
    .build();
```

### SAML Assertion Hooks

Okta's [SAML Assertion Inline Hooks](https://developer.okta.com/use_cases/inline_hooks/saml_hook/saml_hook) docs

#### Replace Attribute

```java
Hooks.builder()
    .samlAssertion(replace("/claims/array/attributeValues/1/value", "replacementValue"))
    .build();
```

#### Add Attribute

```java
 Hooks.builder()
    .samlAssertion(add("/claims/foo", new SamlAssertionCommand.SamlAttribute()
        .setAttributes(Collections.singletonMap("NameFormat", "urn:oasis:names:tc:SAML:2.0:attrname-format:basic"))
        .setAttributeValues(Collections.singletonList(
            new SamlAssertionCommand.SamlAttributeValue()
                .setAttributes(Collections.singletonMap("xsi:type": "xs:string"))
                .setValue("bearer")))))
    .build();
```

## Add Debug Information

Additional debug information can be added to any hook response, these additional fields will be available via [Okta's System Log](https://developer.okta.com/docs/api/resources/system_log/), and as such should NOT contain any secrets.

```java
Hooks.builder()
    .errorCause("An Error")
    .debugContext(Collections.singletonMap("key", "value"))
    .build();
```

## Building the SDK
 
In most cases, you won't need to build the SDK from source. If you want to build it yourself, take a look at the [build instructions wiki](https://github.com/okta/okta-sdk-java/wiki/Build-It) (though just cloning the repo and running `mvn install` should get you going).
 
## Contributing
 
We're happy to accept contributions and PRs! Please see the [contribution guide](CONTRIBUTING.md) to understand how to structure a contribution.

[devforum]: https://devforum.okta.com/
[javadocs]: https://developer.okta.com/okta-hooks-sdk-java/
[lang-landing]: https://developer.okta.com/code/java/
[github-issues]: https://github.com/okta/okta-hooks-sdk-java/issues
[github-releases]: https://github.com/okta/okta-hooks-sdk-java/releases