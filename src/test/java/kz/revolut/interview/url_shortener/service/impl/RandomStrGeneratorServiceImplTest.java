package kz.revolut.interview.url_shortener.service.impl;

import org.junit.jupiter.api.Test;

import java.util.HashSet;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

class RandomStrGeneratorServiceImplTest {

    private final RandomStrGeneratorServiceImpl generator = new RandomStrGeneratorServiceImpl();

    @Test
    void should_generate_string_with_correct_length_when_valid_length_is_given() {
        int length = 8;
        String generated = generator.generate(length);
        assertNotNull(generated);
        assertEquals(length, generated.length());
    }

    @Test
    void should_throw_exception_when_length_is_negative() {
        assertThrows(IllegalArgumentException.class, () -> generator.generate(-1));
    }

    @Test
    void should_throw_exception_when_length_is_zero() {
        assertThrows(IllegalArgumentException.class, () -> generator.generate(0));
    }

    @Test
    void should_generate_different_strings_when_called_multiple_times() {
        int length = 10;
        Set<String> generatedStrings = new HashSet<>();

        for (int i = 0; i < 1000; i++) {
            generatedStrings.add(generator.generate(length));
        }

        assertEquals(1000, generatedStrings.size()); // Ensures uniqueness
    }

    @Test
    void should_generate_only_allowed_characters_when_generating_strings() {
        int length = 12;
        String allowedChars = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";

        String generated = generator.generate(length);
        for (char c : generated.toCharArray()) {
            assertTrue(allowedChars.indexOf(c) != -1, "Generated string contains an invalid character: " + c);
        }
    }

    @Test
    void should_work_correctly_when_called_concurrently() throws InterruptedException {
        int length = 8;
        int threadCount = 50;
        Set<String> results = new HashSet<>();
        Thread[] threads = new Thread[threadCount];

        for (int i = 0; i < threadCount; i++) {
            threads[i] = new Thread(() -> {
                String result = generator.generate(length);
                synchronized (results) {
                    results.add(result);
                }
            });
        }

        for (Thread thread : threads) {
            thread.start();
        }

        for (Thread thread : threads) {
            thread.join();
        }

        assertEquals(threadCount, results.size()); // Ensures uniqueness in multithreading
    }
}