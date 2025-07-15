package org.example.urlshortner.service;

import lombok.RequiredArgsConstructor;
import org.example.urlshortner.dto.AppAuthProperties;
import org.example.urlshortner.entity.AuthToken;
import org.example.urlshortner.repository.AuthTokenRepository;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDateTime;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class AuthTokenService {

    private final AuthTokenRepository tokenRepo;
    private final AppAuthProperties props;
    private final RestTemplate restTemplate = new RestTemplate();

    private final String AUTH_URL = "http://20.244.56.144/evaluation-service/auth";

    public String getValidToken() {
        return tokenRepo.findTopByOrderByIdDesc()
                .filter(token -> token.getExpiresAt().isAfter(LocalDateTime.now()))
                .map(AuthToken::getToken)
                .orElseGet(this::fetchAndStoreNewToken);
    }

    public void invalidateAndRefresh() {
        fetchAndStoreNewToken();
    }

    private String fetchAndStoreNewToken() {
        Map<String, Object> body = Map.of(
                "email", props.getEmail(),
                "name", props.getName(),
                "rollNo", props.getRollNo(),
                "accessCode", props.getAccessCode(),
                "clientID", props.getClientID(),
                "clientSecret", props.getClientSecret()
        );

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<Map<String, Object>> request = new HttpEntity<>(body, headers);

        ResponseEntity<Map> response = restTemplate.postForEntity(AUTH_URL, request, Map.class);

        if (response.getStatusCode().is2xxSuccessful() && response.getBody() != null) {
            String token = (String) response.getBody().get("access_token");
            // default expiry 15 mins (as not mentioned in API)
            LocalDateTime expiry = LocalDateTime.now().plusMinutes(15);
            AuthToken authToken = new AuthToken(null, token, expiry);
            tokenRepo.save(authToken);
            return token;
        }

        throw new RuntimeException("Failed to fetch token: " + response.getStatusCode());
    }
}

