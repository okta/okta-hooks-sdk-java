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

import com.okta.hooks.sdk.commands.SamlAssertionCommand
import org.testng.annotations.Test

import static com.okta.hooks.sdk.commands.SamlAssertionCommand.add
import static com.okta.hooks.sdk.commands.SamlAssertionCommand.replace
import static org.hamcrest.MatcherAssert.assertThat
import static org.hamcrest.Matchers.is

class SamlHooksTest implements HooksSupport {

    @Test
    void replaceBasic() {

        def builder = Hooks.builder()
            .samlAssertion(replace("/claims/array/attributeValues/1/value", "replacementValue"))

        def expectedToString = expected """
        { "commands": [
          { "type": "com.okta.assertion.patch",
            "value": [{
              "op": "replace",
              "path": "/claims/array/attributeValues/1/value",
              "value": "replacementValue"
            }]}
          ]
        }
        """

        assertThat builder.toString(), is(expectedToString)
    }

    @Test
    void replaceComplex() {

        def builder = Hooks.builder()
            .samlAssertion(replace("/authentication/authnContext", [authnContextClassRef: "Something:different?"]))

        def expectedToString = expected """
        { "commands": [
          { "type": "com.okta.assertion.patch",
            "value": [{
              "op": "replace",
              "path": "/authentication/authnContext",
              "value": {
                "authnContextClassRef": "Something:different?"
              }
            }]}
          ]
        }
        """

        assertThat builder.toString(), is(expectedToString)
    }

    @Test
    void basicAdd() {

        SamlAssertionCommand.SamlAttribute samlAttribute = new SamlAssertionCommand.SamlAttribute()
            .setAttributes([NameFormat: "urn:oasis:names:tc:SAML:2.0:attrname-format:basic"])
            .setAttributeValues([
                new SamlAssertionCommand.SamlAttributeValue()
                    .setAttributes(["xsi:type": "xs:string"])
                    .setValue("barer")
        ])

         def builder = Hooks.builder()
            .samlAssertion(add("/claims/foo", samlAttribute))

        def expectedToString = expected """
        { "commands": [
          { "type": "com.okta.assertion.patch",
            "value": [{
              "op": "add",
              "path": "/claims/foo",
              "value": {
                "attributes": {
                  "NameFormat": "urn:oasis:names:tc:SAML:2.0:attrname-format:basic"
                },
                "attributeValues": [{
                  "attributes": {
                    "xsi:type": "xs:string"
                  },
                  "value": "barer"
                }]
              }
            }]}
          ]
        }
        """

        assertThat builder.toString(), is(expectedToString)
    }

    @Test
    void complexAddRemove() {

        def builder = Hooks.builder()
            .samlAssertion(replace("/claims/array/attributeValues/1/value", "replacementValue"))
            .samlAssertion(replace("/authentication/authnContext", [authnContextClassRef: "Something:different?"]))
            .samlAssertion(add("/claims/foo", new SamlAssertionCommand.SamlAttribute()
                    .setAttributes([NameFormat: "urn:oasis:names:tc:SAML:2.0:attrname-format:basic"])
                    .setAttributeValues([
                        new SamlAssertionCommand.SamlAttributeValue()
                            .setAttributes(["xsi:type": "xs:string"])
                            .setValue("bearer")])))
            .samlAssertion(replace("/authentication/sessionIndex", "definitelyARealSession"))

        def expectedToString = expected """
        { "commands": [
            { "type": "com.okta.assertion.patch",
              "value": [{
                "op": "replace",
                "path": "/claims/array/attributeValues/1/value",
                "value": "replacementValue"
              }]
            },
              
            { "type": "com.okta.assertion.patch",
              "value": [{ "op": "replace",
                "path": "/authentication/authnContext",
                "value": {
                  "authnContextClassRef": "Something:different?"
                }
              }]
            },
                
            { "type": "com.okta.assertion.patch",
              "value": [{ "op": "add",
                  "path": "/claims/foo",
                  "value": {
                    "attributes": {
                      "NameFormat": "urn:oasis:names:tc:SAML:2.0:attrname-format:basic"
                    },
                    "attributeValues": [ {
                        "attributes": {
                          "xsi:type": "xs:string"
                        },
                        "value": "bearer"
                      }
                    ]
                  }
                }
              ]
            },
            
            { "type": "com.okta.assertion.patch",
              "value": [ {
                  "op": "replace",
                  "path": "/authentication/sessionIndex",
                  "value": "definitelyARealSession"
                }
              ]
            }
          ]
        }
        """

        assertThat builder.toString(), is(expectedToString)
    }
}