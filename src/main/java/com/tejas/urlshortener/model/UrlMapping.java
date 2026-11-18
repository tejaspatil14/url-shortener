package com.tejas.urlshortener.model;

public record UrlMapping(long id, String originalUrl, String shortCode, long clickCount) {
}
