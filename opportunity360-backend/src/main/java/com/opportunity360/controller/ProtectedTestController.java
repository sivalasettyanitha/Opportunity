
package com.opportunity360.controller;

import java.util.Map;

import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/test")
public class ProtectedTestController {

    @GetMapping("/protected")
    public Map<String, String> protectedApi(
            Authentication authentication) {

        return Map.of(
            "message", "JWT authentication successful!",
            "loggedInUser", authentication.getName(),
            "status", "Authenticated"
        );
    }
}
