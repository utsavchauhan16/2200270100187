package org.example.urlshortner.service;


import lombok.RequiredArgsConstructor;
import org.example.urlshortner.dto.request.UrlRequest;
import org.example.urlshortner.dto.response.UrlResponse;
import org.example.urlshortner.dto.response.UrlStatsResponse;
import org.example.urlshortner.entity.ShortUrl;
import org.example.urlshortner.repository.ShortUrlRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ShortUrlService {

    private final ShortUrlRepository repository;

    public UrlResponse createShortUrl(UrlRequest request) {
        String shortcode = Optional.ofNullable(request.getShortcode()).orElse(generateShortcode());

        if (!shortcode.matches("^[a-zA-Z0-9]{4,20}$")) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Shortcode must be alphanumeric and 4-20 characters long");
        }

        if (repository.existsById(shortcode)) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Custom shortcode already in use");
        }

        int validity = Optional.ofNullable(request.getValidity()).orElse(30);

        ShortUrl shortUrl = new ShortUrl();
        shortUrl.setShortcode(shortcode);
        shortUrl.setOriginalUrl(request.getUrl());
        shortUrl.setCreatedAt(LocalDateTime.now());
        shortUrl.setExpiry(LocalDateTime.now().plusMinutes(validity));

        repository.save(shortUrl);

        return new UrlResponse("http://localhost:8080/" + shortcode, shortUrl.getExpiry());
    }

    public String getOriginalUrl(String shortcode) {
        ShortUrl url = repository.findById(shortcode).orElseThrow(() -> new RuntimeException("Shortcode not found"));

        if (LocalDateTime.now().isAfter(url.getExpiry())) {
            throw new RuntimeException("Shortcode expired");
        }

        url.setRedirectCount(url.getRedirectCount() + 1);
        repository.save(url);
        return url.getOriginalUrl();
    }

    public UrlStatsResponse getStats(String shortcode) {
        ShortUrl url = repository.findById(shortcode).orElseThrow(() -> new RuntimeException("Shortcode not found"));

        return new UrlStatsResponse(shortcode, url.getOriginalUrl(), url.getCreatedAt(), url.getExpiry(), url.getRedirectCount());
    }

    private String generateShortcode() {
        return UUID.randomUUID().toString().substring(0, 6);
    }
}
