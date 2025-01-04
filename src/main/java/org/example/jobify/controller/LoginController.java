package org.example.jobify.controller;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.Map;

@RestController
public class LoginController {

    @Value("${google.client.id}")
    private String clientId;

    @Value("${google.client.secret}")
    private String clientSecret;

    @Value("${google.redirect.uri}")
    private String redirectUri;

    @GetMapping("/login/oauth2/code/google")
    public ResponseEntity<String> handleGoogleLogin(@RequestParam String code) {
        // Exchange the authorization code for an access token
        String token = exchangeCodeForToken(code);

        // After getting the token, you can retrieve user information or store the token
        // This example assumes you're just redirecting to the home page after login
        return ResponseEntity.ok("Token: " + token);  // Redirect to home or return user data
    }

    private String exchangeCodeForToken(String code) {
        String tokenUrl = "https://oauth2.googleapis.com/token";

        // Prepare the request body for the token exchange
        Map<String, String> requestParams = new HashMap<>();
        requestParams.put("code", code);
        requestParams.put("client_id", clientId);
        requestParams.put("client_secret", clientSecret);
        requestParams.put("redirect_uri", redirectUri);
        requestParams.put("grant_type", "authorization_code");

        // Use RestTemplate to send the POST request
        RestTemplate restTemplate = new RestTemplate();
        ResponseEntity<Map> response = restTemplate.postForEntity(tokenUrl, requestParams, Map.class);

        // Extract the access token from the response (assuming the response contains it)
        Map<String, Object> body = response.getBody();
        if (body != null) {
            return (String) body.get("access_token");
        } else {
            throw new RuntimeException("Failed to obtain access token from Google");
        }
    }
}
