package kz.revolut.interview.url_shortener_in_livecoding.impl;

import kz.revolut.interview.url_shortener_in_livecoding.service.GenerateShortUrlService;

import java.util.Random;

public class GenerateShortUrlServiceImpl implements GenerateShortUrlService {

    private final static String CHARS = "ABCDEFGHIJKLMNOPabcdefghijklmnop1234567890";

    @Override
    public String generate(int length) {

        Random random = new Random();
        char[] codes = new char[length];

        for (int i = 0; i < length; i++) {
            codes[i] = CHARS.charAt(random.nextInt(CHARS.length()));
        }

        return new String(codes);

    }

}
