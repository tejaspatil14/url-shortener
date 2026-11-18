package com.tejas.urlshortener.service;

import com.tejas.urlshortener.exception.InvalidUrlException;
import com.tejas.urlshortener.exception.UrlNotFoundException;
import com.tejas.urlshortener.model.UrlMapping;
import com.tejas.urlshortener.repository.UrlRepository;
import org.springframework.stereotype.Service;

import java.net.URI;
import java.net.URISyntaxException;

// Business logic lives here: validation, short code generation, click counting
@Service
public class UrlService {

    private static final String BASE62 =
            "0123456789abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ";
    // Offset so the first codes are not just "1", "2", ...
    private static final long ID_OFFSET = 100000;

    private final UrlRepository repository;

    public UrlService(UrlRepository repository) {
        this.repository = repository;
    }

    public String shorten(String originalUrl) {
        validate(originalUrl);
        long id = repository.insert(originalUrl);
        String code = encode(id + ID_OFFSET);   // unique because the id is unique
        repository.updateShortCode(id, code);
        return code;
    }

    /** Returns the original URL and counts the click. */
    public String resolve(String code) {
        UrlMapping mapping = repository.findByShortCode(code)
                .orElseThrow(() -> new UrlNotFoundException(code));
        repository.incrementClicks(mapping.id());
        return mapping.originalUrl();
    }

    public UrlMapping getStats(String code) {
        return repository.findByShortCode(code)
                .orElseThrow(() -> new UrlNotFoundException(code));
    }

    private void validate(String url) {
        if (url == null || url.isBlank()) {
            throw new InvalidUrlException("URL must not be empty");
        }
        URI uri;
        try {
            uri = new URI(url);
        } catch (URISyntaxException e) {
            throw new InvalidUrlException("URL is not valid");
        }
        String scheme = uri.getScheme();
        boolean httpScheme = "http".equalsIgnoreCase(scheme) || "https".equalsIgnoreCase(scheme);
        if (!httpScheme || uri.getHost() == null) {
            throw new InvalidUrlException("URL must start with http:// or https:// and have a host");
        }
    }

    /** Converts a number to a Base62 string. */
    private String encode(long number) {
        StringBuilder sb = new StringBuilder();
        while (number > 0) {
            sb.append(BASE62.charAt((int) (number % 62)));
            number /= 62;
        }
        return sb.reverse().toString();
    }
}
