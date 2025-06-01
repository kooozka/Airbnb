package com.rental.auth_service.controller;

import com.rental.auth_service.service.JwtTokenService;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.security.Principal;
import java.util.HashMap;
import java.util.Map;

@RestController
public class SecurityAppController {
    private final JwtTokenService jwtTokenService;

    public SecurityAppController(JwtTokenService jwtTokenService) {
        this.jwtTokenService=jwtTokenService;
    }

    @RequestMapping("/")
    public String home(){
        return "Welcome!";
    }

    @RequestMapping("/user")
    public Principal user(Principal user) {
        return user;
    }

    @RequestMapping("/token")
    public Map<String, String> getToken(OAuth2AuthenticationToken authenticationToken) {
        Map<String, Object> attributes = authenticationToken.getPrincipal().getAttributes();
        String email = (String) attributes.get("email");

        return jwtTokenService.generateToken(email);
    }

    @RequestMapping("/validate-token")
    public Map<String, String> validateToken(@RequestParam String token) {
        Map<String, String> response = new HashMap<>();

        try {
            if(!jwtTokenService.isTokenExpired(token)) {
                String email =jwtTokenService.extractEmail(token);
                response.put("valid", "true");
                response.put("email", email);
                response.put("expiresAt", jwtTokenService.extractExpiration(token).toString());
            } else {
                response.put("valid", "false");
                response.put("error", "Token expired");
            }
        } catch (Exception e) {
            response.put("valid", "false");
            response.put("error", "Invalid token");
        }
        return response;
    }
}
