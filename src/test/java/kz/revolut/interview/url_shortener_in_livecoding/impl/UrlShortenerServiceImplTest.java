package kz.revolut.interview.url_shortener_in_livecoding.impl;

import kz.revolut.interview.url_shortener_in_livecoding.GenerateShortUrlServiceForTests;
import kz.revolut.interview.url_shortener_in_livecoding.exceptions.MaximumStoredUrlException;
import kz.revolut.interview.url_shortener_in_livecoding.exceptions.ShortUrlAlreadyOccupiedException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static kz.revolut.interview.url_shortener_in_livecoding.Constants.SHORT_URL_DOMAIN_STARTER;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

class UrlShortenerServiceImplTest {

    private UrlShortenerServiceImpl urlShortenerService;

    @BeforeEach
    void init() {
        urlShortenerService = new UrlShortenerServiceImpl(100,
                10,
                new GenerateShortUrlServiceImpl());
    }

    @Test
    void generateShorUrl__throwMaximumStoredNumException() {

        UrlShortenerServiceImpl urlShortenerService = new UrlShortenerServiceImpl(2,
                10,
                new GenerateShortUrlServiceImpl());

        //
        //
        String shortUrl0 = urlShortenerService.generateShorUrl("qped4wvVC1eASuRQ");
        String shortUrl1 = urlShortenerService.generateShorUrl("DHtgf9GXHDajR9hi");
        assertThrows(MaximumStoredUrlException.class,
                () -> urlShortenerService.generateShorUrl("SBBukOWCiRozkNOT"));
        //
        //

        assertThat(shortUrl0).contains(SHORT_URL_DOMAIN_STARTER);
        assertThat(shortUrl1).contains(SHORT_URL_DOMAIN_STARTER);

        assertThat(shortUrl0).hasSize(SHORT_URL_DOMAIN_STARTER.length() + 10);
        assertThat(shortUrl1).hasSize(SHORT_URL_DOMAIN_STARTER.length() + 10);

    }

    @Test
    void generateShorUrl__alreadyHasAnShortUrl() {

        String url = "dNPnvV90RhUaQj31";

        String shortUrl0 = urlShortenerService.generateShorUrl(url);

        //
        //
        String shortUrl1 = urlShortenerService.generateShorUrl(url);
        //
        //

        assertThat(shortUrl0).isEqualTo(shortUrl1);

    }

    @Test
    void generateShorUrl__shortUrlAlreadyOccupied() {

        String url = "dNPnvV90RhUaQj31";
        String url1 = "AV7Av36O7HxhQTms";

        UrlShortenerServiceImpl urlShortenerService = new UrlShortenerServiceImpl(
                20,
                7,
                new GenerateShortUrlServiceForTests());

        urlShortenerService.generateShorUrl(url);

        //
        //
        assertThrows(ShortUrlAlreadyOccupiedException.class,
                () -> urlShortenerService.generateShorUrl(url1));
        //
        //

    }

}
