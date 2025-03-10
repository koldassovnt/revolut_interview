package kz.revolut.interview.url_shortener.service;

public interface UrlShortenerService {

    String shortUrl(String url);

    String getOriginalUrl(String shortUrl);

}