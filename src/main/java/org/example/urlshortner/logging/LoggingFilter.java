package org.example.urlshortner.logging;

import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.example.urlshortner.service.AuthTokenService;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
@Component
@RequiredArgsConstructor
public class LoggingFilter implements Filter {

    private final AuthTokenService tokenService;

    @Override
    public void doFilter(ServletRequest req, ServletResponse res, FilterChain chain)
            throws IOException, ServletException {

        HttpServletRequest request = (HttpServletRequest) req;
        HttpServletResponse response = (HttpServletResponse) res;

        String logMessage = String.format("Log(stack=UrlShortener, level=INFO, package=%s, message=%s %s)",
                request.getRequestURI(), request.getMethod(), request.getRequestURL());

        String token = tokenService.getValidToken();
        sendLogToEvaluationServer(token, logMessage, tokenService);

        chain.doFilter(req, res);
    }

    private void sendLogToEvaluationServer(String token, String logMessage, AuthTokenService tokenService) {
        try {
            HttpURLConnection conn = (HttpURLConnection)
                    new URL("http://20.244.56.144/evaluation-service/logs").openConnection();
            conn.setRequestMethod("POST");
            conn.setDoOutput(true);
            conn.setRequestProperty("Content-Type", "application/json");
            conn.setRequestProperty("Authorization", "Bearer " + token);

            String jsonPayload = "{\"log\":\"" + logMessage + "\"}";
            System.out.println("Sending log: " + jsonPayload);
            System.out.println("Using token: " + token);
            System.out.println("To URL: " + conn.getURL());
            System.out.println("Request Method: " + conn.getRequestMethod());
            System.out.println("Request Headers: " + conn.getRequestProperties());
            System.out.println("Request Body: " + jsonPayload);
            try (OutputStream os = conn.getOutputStream()) {
                os.write(jsonPayload.getBytes(StandardCharsets.UTF_8));
            }

            int status = conn.getResponseCode();
            if (status == HttpServletResponse.SC_UNAUTHORIZED) {
                // Token expired or invalid → refresh and retry once
                tokenService.invalidateAndRefresh();
            }

        } catch (Exception e) {
            System.out.println("Log send failed: " + e.getMessage());
        }
    }
}
