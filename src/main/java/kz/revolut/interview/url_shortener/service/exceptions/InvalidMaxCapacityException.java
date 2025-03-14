package kz.revolut.interview.url_shortener.service.exceptions;

public class InvalidMaxCapacityException extends RuntimeException {
    public InvalidMaxCapacityException(String message) {
        super(message);
    }
}