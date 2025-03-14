package kz.revolut.interview.url_shortener_in_livecoding;


import kz.revolut.interview.url_shortener_in_livecoding.service.GenerateShortUrlService;

public class GenerateShortUrlServiceForTests implements GenerateShortUrlService {

    @Override
    public String generate(int length) {
        return "mDq858j1";
    }

}