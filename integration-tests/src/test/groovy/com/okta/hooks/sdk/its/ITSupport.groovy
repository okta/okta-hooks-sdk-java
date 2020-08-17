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

import com.okta.hooks.sdk.its.app.HooksExampleApplication
import com.okta.sdk.client.Client
import com.okta.sdk.client.Clients
import com.okta.sdk.resource.Deletable
import com.okta.sdk.resource.ExtensibleResource
import com.okta.sdk.resource.application.Application
import com.okta.sdk.resource.group.rule.GroupRule
import com.okta.sdk.resource.user.User
import com.okta.sdk.resource.user.UserBuilder
import groovy.json.JsonSlurper
import org.apache.commons.lang3.RandomStringUtils
import org.apache.sshd.client.SshClient
import org.apache.sshd.client.channel.ClientChannel
import org.apache.sshd.client.keyverifier.AcceptAllServerKeyVerifier
import org.apache.sshd.common.session.Session
import org.apache.sshd.common.util.net.SshdSocketAddress
import org.apache.sshd.server.forward.AcceptAllForwardingFilter
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.boot.SpringApplication
import org.springframework.context.ConfigurableApplicationContext
import org.testng.Assert
import org.testng.annotations.AfterClass
import org.testng.annotations.AfterMethod
import org.testng.annotations.BeforeClass

import java.util.concurrent.TimeUnit

import static org.hamcrest.MatcherAssert.assertThat
import static org.hamcrest.Matchers.containsString
import static org.hamcrest.Matchers.startsWith

class ITSupport {

    private static final Logger log = LoggerFactory.getLogger(ITSupport)

    private List<Deletable> toBeDeleted = []
    private List<String> hooksToBeDeleted = []
    private ThreadLocal<Client> threadLocal = new ThreadLocal<>()

    private final String relativeBasedHookPath
    private final String hookType

    ConfigurableApplicationContext applicationContext
    Session sshSession
    String remoteTestUrl

    ITSupport(String relativeBasedHookPath, String hookType) {
        this.relativeBasedHookPath = relativeBasedHookPath
        this.hookType = hookType
    }

    Client getClient() {
        Client client = threadLocal.get()
        if (client == null) {
            threadLocal.set(buildClient())
        }
        return threadLocal.get()
    }

    private Client buildClient() {
        return Clients.builder().build()
    }

    /**
     * Registers a Deletable to be cleaned up after the test is run.
     * @param deletable Resource to be deleted.
     */
    void registerForCleanup(Deletable deletable) {
        toBeDeleted.add(deletable)
    }

    @AfterMethod
    void clean() {
        toBeDeleted.reverse().each { deletable ->
            try {
                if (deletable instanceof User) {
                    deletable.deactivate()
                } else if (deletable instanceof GroupRule) {
                    deletable.deactivate()
                } else if (deletable instanceof Application) {
                    deletable.deactivate()
                }
                deletable.delete()
            }
            catch (Exception e) {
                log.trace("Exception thrown during cleanup, it is ignored so the rest of the cleanup can be run:", e)
            }
        }
    }

    static String randomUUID() {
        return UUID.randomUUID().toString()
    }

    static String randomEmail() {
        return "joe.coder+" + randomUUID() + "@example.com"
    }

    User randomUser(email = randomEmail()) {
        Client client = getClient()

        User user = UserBuilder.instance()
                .setEmail(email)
                .setFirstName("Joe")
                .setLastName("Code")
                .setPassword("Password1".toCharArray())
                .setSecurityQuestion("Favorite security question?")
                .setSecurityQuestionAnswer("None of them!")
                .setActive(true)
                .buildAndCreate(client)
        registerForCleanup(user)

        return user
    }

    void startTunnel() {

        SshClient client = SshClient.setUpDefaultClient()
        client.setForwardingFilter(AcceptAllForwardingFilter.INSTANCE)
        client.setServerKeyVerifier(AcceptAllServerKeyVerifier.INSTANCE)
        client.start()

        // the user doesn't matter but it is a required arg
        String usename = RandomStringUtils.random(10, true, true)
        sshSession = client.connect(usename, "ssh.localhost.run", 22).verify().getSession()

        sshSession.auth().verify(5L, TimeUnit.SECONDS)

        ClientChannel channel = sshSession.createShellChannel()
        PipedOutputStream pipeOut = new PipedOutputStream()
        PipedInputStream pipeIn = new PipedInputStream(pipeOut)
        channel.setOut(pipeOut)
        channel.open().verify(7L, TimeUnit.SECONDS)

        BufferedReader reader = new BufferedReader(new InputStreamReader(pipeIn))

        def local = new SshdSocketAddress("localhost", getHooksAppPort())
        def remote = new SshdSocketAddress("localhost",80)

        sshSession.startRemotePortForwarding(remote, local)

        // get the random-ish URL from the log
        reader.readLine()
        def line2 = reader.readLine()
        assertThat "Expected to find domain in second SSH log line", line2, containsString('"domain"')

        def localhostRunData = new JsonSlurper().parseText(line2)
        remoteTestUrl = "https://" + localhostRunData.domain

        // re-target output to System.out
        channel.setOut(System.out)
    }

    void stopTunnel() {
        if (sshSession != null) {
            sshSession.close()
        }
    }

    static int getFreePort() {
        return new ServerSocket(0).withCloseable {it.getLocalPort()}
    }

    void startHooksApp(int port = getFreePort()) {
        applicationContext = SpringApplication.run(HooksExampleApplication, "--server.port=${port}")
        println("hooks app port: " + getHooksAppPort())
    }

    int getHooksAppPort() {
        return applicationContext.getEnvironment().getProperty("server.port") as int
    }

    void stopHooksApp() {
        if (applicationContext != null && applicationContext.isRunning()) {
            applicationContext.stop()
        } else {
            log.warn("Hooks Application was not running")
        }
    }

    static <T extends Throwable> T expect(Class<T> catchMe, Closure closure) {
        try {
            closure.call()
            Assert.fail("Expected ${catchMe.getName()} to be thrown.")
        } catch(e) {
            if (!e.class.isAssignableFrom(catchMe)) {
                throw e
            }
            return e
        }
    }

    String createHook(String path) {
        return createHook("${relativeBasedHookPath}/${path}", hookType)
    }

    String createHook(String path, String type) {

        def uuid = UUID.randomUUID().toString()

        ExtensibleResource hooksResource = client.instantiate(ExtensibleResource)
        hooksResource.putAll([
            name: "test-hook+${uuid}".toString(),
            type: type,
            version: "1.0.0",
            channel: [
                type: "HTTP",
                version: "1.0.0",
                config: [
                    uri: "${remoteTestUrl}/${path}".toString(),
                    authScheme: [
                        type: "HEADER",
                        key: "Authorization",
                        value: "api-key-here"
                    ]
                ]
            ]
        ])

        def client = getClient()
        def hookResponse = client.getDataStore().http()
                .setBody(hooksResource)
                .post("/api/v1/inlineHooks", ExtensibleResource)

        def id = hookResponse.getString("id")
        hooksToBeDeleted.add(id)

        return id
    }

    @BeforeClass
    void startTestAppAndTunnel() {
        startHooksApp()
        startTunnel()
    }

    @AfterClass
    void stopTestAppAndTunnel() {
        stopTunnel()
        stopHooksApp()
    }

    @AfterClass
    void cleanupHooks() {
        hooksToBeDeleted.forEach {
            def client = getClient()
            client.getDataStore().http().post("/api/v1/inlineHooks/${it}/lifecycle/deactivate")
            client.getDataStore().http().delete("/api/v1/inlineHooks/${it}")
        }
    }
}
