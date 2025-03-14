package kz.revolut.interview.url_shortener.service.exceptions;

public class MaxCapacityReachedException extends RuntimeException {
    public MaxCapacityReachedException(String message) {
        super(message);
    }
}