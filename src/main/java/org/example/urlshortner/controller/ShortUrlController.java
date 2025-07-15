package org.example.urlshortner.controller;

import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.example.urlshortner.dto.request.UrlRequest;
import org.example.urlshortner.dto.response.UrlResponse;
import org.example.urlshortner.dto.response.UrlStatsResponse;
import org.example.urlshortner.service.ShortUrlService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;

@RestController
@RequiredArgsConstructor
public class ShortUrlController {


    @Autowired
    private final ShortUrlService service;

    @PostMapping("/shorturls")
    public ResponseEntity<UrlResponse> create(@RequestBody UrlRequest request) {
        return ResponseEntity.ok(service.createShortUrl(request));
    }

    @GetMapping("/{shortcode}")
    public void redirect(@PathVariable String shortcode, HttpServletResponse response) throws IOException {
        try {
            String originalUrl = service.getOriginalUrl(shortcode);
            response.sendRedirect(originalUrl);
        } catch (RuntimeException e) {
            response.sendError(HttpServletResponse.SC_NOT_FOUND, e.getMessage());
        }
    }

    @GetMapping("/shorturls/{shortcode}/stats")
    public ResponseEntity<UrlStatsResponse> stats(@PathVariable String shortcode) {
        return ResponseEntity.ok(service.getStats(shortcode));
    }
}
