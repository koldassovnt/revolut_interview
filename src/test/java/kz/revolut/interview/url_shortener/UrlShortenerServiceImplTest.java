package kz.revolut.interview.url_shortener;

import kz.revolut.interview.ParentTest;
import kz.revolut.interview.RandomStrGeneratorServiceForTest;
import kz.revolut.interview.url_shortener.service.RandomStrGeneratorService;
import kz.revolut.interview.url_shortener.service.impl.UrlShortenerServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.*;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

class UrlShortenerServiceImplTest extends ParentTest {

    private UrlShortenerServiceImpl urlShortenerService;

    @BeforeEach
    void init() {
        RandomStrGeneratorService randomStrGeneratorService = new RandomStrGeneratorServiceForTest();

        urlShortenerService = new UrlShortenerServiceImpl(
                7,
                5,
                randomStrGeneratorService);
    }

    @Test
    void shortUrl__nullOrBlank() {

        //
        //
        assertThrows(IllegalArgumentException.class, () -> urlShortenerService.shortUrl(null));
        assertThrows(IllegalArgumentException.class, () -> urlShortenerService.shortUrl(""));
        assertThrows(IllegalArgumentException.class, () -> urlShortenerService.shortUrl("   "));
        //
        //

    }

    @Test
    void shortUrl__firstCall() {

        String url = "fLeI5WJOJd8n3vBPzWtSb6VV";

        //
        //
        String shortUrl = urlShortenerService.shortUrl(url);
        //
        //

        assertThat(shortUrl).hasSize(7);

    }

    @Test
    void shortUrl__sameUrl() {

        String url = "fLeI5WJOJd8n3vBPzWtSb6VV";
        String shortUrl = urlShortenerService.shortUrl(url);

        //
        //
        String shortUrl2 = urlShortenerService.shortUrl(url);
        //
        //

        assertThat(shortUrl2).hasSize(7);
        assertThat(shortUrl2).isEqualTo(shortUrl);

    }

    @Test
    void shortUrl__reachRetryLimit() {

        UrlShortenerServiceImpl urlShortenerService = new UrlShortenerServiceImpl(
                7,
                2,
                new RandomStrGeneratorServiceForTest("uDpQo4jX"));

        String url0 = "fLeI5WJOJd8n3vBPzWtSb6VV";
        String url1 = "Ae1s7lcSor86MD7i2sfONOVe";

        urlShortenerService.shortUrl(url0);

        //
        //
        assertThrows(RuntimeException.class,
                () -> urlShortenerService.shortUrl(url1));
        //
        //

    }

    @Test
    void shortUrl__multiThreading() throws ExecutionException, InterruptedException {

        try (ExecutorService executorService = Executors.newFixedThreadPool(10)) {
            Set<String> generatedShortUrls = Collections.synchronizedSet(new HashSet<>());
            Set<Future<String>> futures = new HashSet<>();

            for (int i = 0; i < 100; i++) {
                String url = "https://example.com/page" + i;

                //
                //
                futures.add(executorService.submit(() -> urlShortenerService.shortUrl(url)));
                //
                //

            }

            for (Future<String> future : futures) {
                generatedShortUrls.add(future.get());
            }

            assertThat(generatedShortUrls).hasSize(100);

        }

    }

    @Test
    void getOriginalUrl__noOriginal() {

        String shortUrl = "W8hV0Xes";

        //
        //
        assertThrows(RuntimeException.class,
                () -> urlShortenerService.getOriginalUrl(shortUrl));
        //
        //

    }

    @Test
    void getOriginalUrl() {

        String original = "VISLlViW46cEQA7HuVru7HSh";
        String shortUrl = urlShortenerService.shortUrl(original);

        //
        //
        String originalUrl = urlShortenerService.getOriginalUrl(shortUrl);
        //
        //

        assertThat(originalUrl).isEqualTo(original);

    }

}