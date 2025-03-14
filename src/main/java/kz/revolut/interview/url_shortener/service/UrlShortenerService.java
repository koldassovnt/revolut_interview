package kz.revolut.interview.url_shortener.service;

import kz.revolut.interview.url_shortener.service.model.UrlModel;

public interface UrlShortenerService {

    UrlModel shortUrl(String url);

    String generateUniqueShortUrl();

}