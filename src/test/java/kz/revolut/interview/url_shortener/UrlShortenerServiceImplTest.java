package kz.revolut.interview.url_shortener;

import kz.revolut.interview.url_shortener.service.RandomStrGeneratorService;
import kz.revolut.interview.url_shortener.service.exceptions.InvalidMaxCapacityException;
import kz.revolut.interview.url_shortener.service.exceptions.InvalidShortUrlLengthException;
import kz.revolut.interview.url_shortener.service.exceptions.MaxCapacityReachedException;
import kz.revolut.interview.url_shortener.service.exceptions.MaxRetryAttemptsReachedException;
import kz.revolut.interview.url_shortener.service.impl.UrlShortenerServiceImpl;
import kz.revolut.interview.url_shortener.service.model.UrlModel;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.parallel.Execution;
import org.junit.jupiter.api.parallel.ExecutionMode;
import org.mockito.Mockito;

import java.math.BigInteger;
import java.util.*;
import java.util.concurrent.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

class UrlShortenerServiceImplTest {

    private RandomStrGeneratorService randomStrGeneratorService;
    private UrlShortenerServiceImpl urlShortenerService;

    private static final int SHORT_URL_LENGTH = 10;
    private static final int MAX_CAPACITY = 100;

    @BeforeEach
    void setUp() {
        randomStrGeneratorService = Mockito.mock(RandomStrGeneratorService.class);
        urlShortenerService = new UrlShortenerServiceImpl(SHORT_URL_LENGTH, MAX_CAPACITY, randomStrGeneratorService);
    }

    @Test
    void should_throw_exception_for_invalid_short_url_length() {
        assertThrows(InvalidShortUrlLengthException.class, () ->
                new UrlShortenerServiceImpl(0, MAX_CAPACITY, randomStrGeneratorService));
    }

    @Test
    void should_throw_exception_for_invalid_max_capacity() {
        assertThrows(InvalidMaxCapacityException.class, () ->
                new UrlShortenerServiceImpl(SHORT_URL_LENGTH, 0, randomStrGeneratorService));
    }

    @Test
    void should_throw_exception_for_null_or_empty_url() {
        assertThrows(IllegalArgumentException.class, () -> urlShortenerService.shortUrl(null));
        assertThrows(IllegalArgumentException.class, () -> urlShortenerService.shortUrl(" "));
    }

    @Test
    void should_return_same_short_url_for_same_original_url() {
        when(randomStrGeneratorService.generate(SHORT_URL_LENGTH)).thenReturn("abc123");

        UrlModel firstCall = urlShortenerService.shortUrl("https://example.com");
        UrlModel secondCall = urlShortenerService.shortUrl("https://example.com");

        assertEquals(firstCall, secondCall);
    }

    @Test
    void should_generate_unique_short_urls_for_different_original_urls() {
        when(randomStrGeneratorService.generate(SHORT_URL_LENGTH)).thenReturn("abc123", "xyz789");

        UrlModel first = urlShortenerService.shortUrl("https://example1.com");
        UrlModel second = urlShortenerService.shortUrl("https://example2.com");

        assertNotEquals(first.shortUrl(), second.shortUrl());
    }

    @Test
    void should_throw_exception_when_capacity_reached() {
        UrlShortenerServiceImpl smallCapacityService = new UrlShortenerServiceImpl(
                SHORT_URL_LENGTH,
                1,
                randomStrGeneratorService);
        when(randomStrGeneratorService.generate(SHORT_URL_LENGTH)).thenReturn("abc123", "xyz789");

        smallCapacityService.shortUrl("https://example.com");
        assertThrows(MaxCapacityReachedException.class, () -> smallCapacityService.shortUrl("https://example2.com"));
    }

    @Test
    void should_throw_exception_when_max_retries_exceeded() {
        when(randomStrGeneratorService.generate(SHORT_URL_LENGTH)).thenReturn("abc123");
        urlShortenerService.shortUrl("https://example.com");

        assertThrows(MaxRetryAttemptsReachedException.class, () -> urlShortenerService.shortUrl("https://example2.com"));
    }

    @Test
    @Execution(ExecutionMode.CONCURRENT)
    void should_handle_concurrent_url_shortening() throws InterruptedException, ExecutionException {
        Set<String> shortUrls = ConcurrentHashMap.newKeySet();
        List<String> failedUrls = Collections.synchronizedList(new ArrayList<>());

        try (ExecutorService executorService = Executors.newFixedThreadPool(10)) {
            when(randomStrGeneratorService.generate(SHORT_URL_LENGTH))
                    .thenAnswer(invocation -> new BigInteger(130, ThreadLocalRandom.current())
                            .toString(32)
                            .substring(0, SHORT_URL_LENGTH));

            Callable<Void> task = () -> {
                try {
                    String url = "https://example.com/" + ThreadLocalRandom.current().nextInt(1000);
                    UrlModel urlModel = urlShortenerService.shortUrl(url);
                    shortUrls.add(urlModel.shortUrl());
                } catch (Exception e) {
                    failedUrls.add("Failure");
                }
                return null;
            };

            List<Future<Void>> futures = new ArrayList<>();
            for (int i = 0; i < 50; i++) {
                futures.add(executorService.submit(task));
            }

            for (Future<Void> future : futures) {
                future.get();
            }

            executorService.shutdown();
        }

        System.out.println("Failures: " + failedUrls.size());
        assertEquals(50, shortUrls.size() + failedUrls.size()); // Ensure all attempts are accounted for
    }

}