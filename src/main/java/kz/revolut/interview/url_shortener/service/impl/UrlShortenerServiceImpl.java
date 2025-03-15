package kz.revolut.interview.url_shortener.service.impl;

import kz.revolut.interview.url_shortener.service.RandomStrGeneratorService;
import kz.revolut.interview.url_shortener.service.UrlShortenerService;
import kz.revolut.interview.url_shortener.service.exceptions.InvalidMaxCapacityException;
import kz.revolut.interview.url_shortener.service.exceptions.InvalidShortUrlLengthException;
import kz.revolut.interview.url_shortener.service.exceptions.MaxCapacityReachedException;
import kz.revolut.interview.url_shortener.service.exceptions.MaxRetryAttemptsReachedException;
import kz.revolut.interview.url_shortener.service.model.UrlModel;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class UrlShortenerServiceImpl implements UrlShortenerService {

    private static final int MAX_RETRY_ATTEMPTS = 10;

    private final Map<String, String> urlToShortMap = new ConcurrentHashMap<>();
    private final Map<String, String> shortToUrlMap = new ConcurrentHashMap<>();

    private final RandomStrGeneratorService randomStrGeneratorService;
    private final int shortUrlLength;
    private final int maxCapacity;

    public UrlShortenerServiceImpl(int shortUrlLength,
                                   int maxCapacity,
                                   RandomStrGeneratorService randomStrGeneratorService) {
        if (shortUrlLength <= 0) {
            throw new InvalidShortUrlLengthException("Invalid short URL length");
        }

        if (maxCapacity <= 0) {
            throw new InvalidMaxCapacityException("Invalid max capacity");
        }

        this.shortUrlLength = shortUrlLength;
        this.maxCapacity = maxCapacity;
        this.randomStrGeneratorService = randomStrGeneratorService;
    }

    @Override
    public UrlModel shortUrl(String url) {
        if (url == null || url.isBlank()) {
            throw new IllegalArgumentException("Provided url is null");
        }

        if (urlToShortMap.containsKey(url)) {
            return new UrlModel(url, urlToShortMap.get(url));
        }

        synchronized (this) {
            if (urlToShortMap.size() >= maxCapacity) {
                throw new MaxCapacityReachedException("Reached capacity limit");
            }

            int attemptCounter = 0;

            while (attemptCounter < MAX_RETRY_ATTEMPTS) {
                String shortUrl = randomStrGeneratorService.generate(shortUrlLength);
                attemptCounter++;

                if (shortToUrlMap.putIfAbsent(shortUrl, url) == null) {
                    urlToShortMap.put(url, shortUrl);
                    return new UrlModel(url, shortUrl);
                }
            }
        }

        throw new MaxRetryAttemptsReachedException("Failed to generate a unique short URL after " +
                MAX_RETRY_ATTEMPTS + " attempts");
    }

}