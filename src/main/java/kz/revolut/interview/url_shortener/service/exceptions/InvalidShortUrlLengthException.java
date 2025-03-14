package kz.revolut.interview.url_shortener.service.exceptions;

public class InvalidShortUrlLengthException extends RuntimeException {
    public InvalidShortUrlLengthException(String message) {
        super(message);
    }
}