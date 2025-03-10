package kz.revolut.interview.url_shortener.service.impl;

import kz.revolut.interview.url_shortener.service.RandomStrGeneratorService;

import java.util.concurrent.ThreadLocalRandom;

public class RandomStrGeneratorServiceImpl implements RandomStrGeneratorService {

    private static final String CHARACTERS = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";

    @Override
    public String generate(int length) {

        char[] codes = new char[length];
        ThreadLocalRandom random = ThreadLocalRandom.current();

        for (int i = 0; i < length; i++) {
            codes[i] = CHARACTERS.charAt(random.nextInt(CHARACTERS.length()));
        }

        return new String(codes);

    }

}
