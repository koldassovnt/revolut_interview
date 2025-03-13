package kz.revolut.interview.url_shortener.service.impl;

import kz.revolut.interview.url_shortener.service.RandomStrGeneratorService;
import kz.revolut.interview.url_shortener.service.UrlShortenerService;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class UrlShortenerServiceImpl implements UrlShortenerService {

    private final Map<String, String> urlToShortMap = new ConcurrentHashMap<>();
    private final Map<String, String> shortToUrlMap = new ConcurrentHashMap<>();

    private final RandomStrGeneratorService randomStrGeneratorService;
    private final int generateShortUrlRetryLimit;
    private final int shortUrlLength;

    public UrlShortenerServiceImpl(int shortUrlLength,
                                   int generateShortUrlRetryLimit,
                                   RandomStrGeneratorService randomStrGeneratorService) {
        this.shortUrlLength = shortUrlLength;
        this.generateShortUrlRetryLimit = generateShortUrlRetryLimit;
        this.randomStrGeneratorService = randomStrGeneratorService;
    }

    @Override
    public String shortUrl(String url) {
        if (url == null || url.isBlank()) {
            throw new IllegalArgumentException("Provided url is null");
        }

        return urlToShortMap.computeIfAbsent(url, key -> {
            String shortUrl = generateUniqueShortUrl();
            shortToUrlMap.put(shortUrl, url);
            return shortUrl;
        });
    }

    @Override
    public String getOriginalUrl(String shortUrl) {
        String original = shortToUrlMap.get(shortUrl);

        if (original == null) {
            throw new RuntimeException("Short url does not have original url");
        }

        return original;
    }

    private String generateUniqueShortUrl() {

        String shortUrl;
        int generateCount = 0;

        do {

            if (generateCount == generateShortUrlRetryLimit) {
                throw new RuntimeException("Reached generate short url retry limit");
            }

            shortUrl = randomStrGeneratorService.generate(shortUrlLength);
            generateCount++;

        } while (shortToUrlMap.containsKey(shortUrl));

        return shortUrl;

    }

}