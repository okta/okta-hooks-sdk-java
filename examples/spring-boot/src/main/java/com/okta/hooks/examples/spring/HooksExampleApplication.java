package com.okta.hooks.examples.spring;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.okta.hooks.sdk.Hooks;
import com.okta.hooks.sdk.models.HookResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import javax.json.JsonObject;
import java.io.IOException;
import java.util.Arrays;

import static com.okta.hooks.sdk.models.OAuth2Command.addAccessTokenClaim;
import static com.okta.hooks.sdk.models.OAuth2Command.addIdTokenClaim;
import static com.okta.hooks.sdk.models.UserRegistrationCommand.denyRegistration;

@RestController
@SpringBootApplication
public class HooksExampleApplication {

//    @Autowired
//    private final ObjectMapper objectMapper;

    public static void main(String[] args) {
        SpringApplication.run(HooksExampleApplication.class, args);
    }

    @PostMapping("oauth2")
    public @ResponseBody HookResponse oauth2(@RequestBody String request) throws IOException {

        return Hooks.builder()
                .error("NO NO NO")
                .oauth2(addIdTokenClaim("myClaimKey", "my super cool value"),
                        addAccessTokenClaim("hello", Arrays.asList("a", "list", "value")))
                .build();
    }

    @PostMapping("user-reg")
    public @ResponseBody HookResponse userReg(@RequestBody String request) throws IOException {

        System.out.println(request);

        return Hooks.builder()
                .error("<a href=\"#bad\">this is bad</a>")
//                .userRegistration(denyRegistration("No soup for you!"))
                .build();
    }
}
