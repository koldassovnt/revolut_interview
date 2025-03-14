package kz.revolut.interview.url_shortener.service.exceptions;

public class MaxRetryAttemptsReachedException extends RuntimeException {
    public MaxRetryAttemptsReachedException(String message) {
        super(message);
    }
}