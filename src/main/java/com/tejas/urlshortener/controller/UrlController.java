package com.tejas.urlshortener.controller;

import com.tejas.urlshortener.model.ShortenRequest;
import com.tejas.urlshortener.model.UrlMapping;
import com.tejas.urlshortener.service.UrlService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.Map;

@RestController
public class UrlController {

    private final UrlService service;

    public UrlController(UrlService service) {
        this.service = service;
    }

    // POST /shorten   body: {"url": "https://example.com/very/long/link"}
    @PostMapping("/shorten")
    public ResponseEntity<Map<String, String>> shorten(@RequestBody ShortenRequest request) {
        String code = service.shorten(request.url());
        String shortUrl = ServletUriComponentsBuilder.fromCurrentContextPath()
                .path("/{code}").buildAndExpand(code).toUriString();
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(Map.of("shortCode", code, "shortUrl", shortUrl));
    }

    // GET /{code}  -> 302 redirect to the original URL
    @GetMapping("/{code}")
    public ResponseEntity<Void> redirect(@PathVariable String code) {
        String original = service.resolve(code);
        return ResponseEntity.status(HttpStatus.FOUND)
                .location(URI.create(original))
                .build();
    }

    // GET /stats/{code} -> original URL and click count
    @GetMapping("/stats/{code}")
    public UrlMapping stats(@PathVariable String code) {
        return service.getStats(code);
    }
}
