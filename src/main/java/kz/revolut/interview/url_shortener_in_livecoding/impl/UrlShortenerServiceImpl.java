package kz.revolut.interview.url_shortener_in_livecoding.impl;

import kz.revolut.interview.url_shortener_in_livecoding.exceptions.MaximumStoredUrlException;
import kz.revolut.interview.url_shortener_in_livecoding.exceptions.ShortUrlAlreadyOccupiedException;
import kz.revolut.interview.url_shortener_in_livecoding.service.GenerateShortUrlService;
import kz.revolut.interview.url_shortener_in_livecoding.service.UrlShortenerService;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.ReentrantLock;

import static kz.revolut.interview.url_shortener_in_livecoding.Constants.SHORT_URL_DOMAIN_STARTER;

public class UrlShortenerServiceImpl implements UrlShortenerService {

    private final Map<String, String> originalToShortMap = new ConcurrentHashMap<>();
    private final Map<String, String> shortToOriginalMap = new ConcurrentHashMap<>();

    private final ReentrantLock lock = new ReentrantLock();

    private final GenerateShortUrlService generateShortUrlService;
    private final int maximumNumberOfStoredUrl;
    private final int shortUrlSize;

    public UrlShortenerServiceImpl(int maximumNumberOfStoredUrl,
                                   int shortUrlSize,
                                   GenerateShortUrlService generateShortUrlService) {
        this.maximumNumberOfStoredUrl = maximumNumberOfStoredUrl;
        this.generateShortUrlService = generateShortUrlService;
        this.shortUrlSize = shortUrlSize;
    }

    @Override
    public String generateShorUrl(String url) {

        try {

            lock.lock();

            if (originalToShortMap.size() == maximumNumberOfStoredUrl) {
                throw new MaximumStoredUrlException("Reached maximumNumberOfStoredUrl number");
            }

            if (originalToShortMap.containsKey(url)) {
                return originalToShortMap.get(url);
            }

            String shortUrl =
                    SHORT_URL_DOMAIN_STARTER +
                            generateShortUrlService.generate(shortUrlSize);

            if (shortToOriginalMap.containsKey(shortUrl)) {
                throw new ShortUrlAlreadyOccupiedException("Short url occupied");
            }

            originalToShortMap.put(url, shortUrl);
            shortToOriginalMap.put(shortUrl, url);

            return shortUrl;

        } finally {
            lock.unlock();
        }

    }
}
