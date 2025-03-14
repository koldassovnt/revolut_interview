package kz.revolut.interview.url_shortener_in_livecoding.exceptions;

public class MaximumStoredUrlException extends RuntimeException {

    public MaximumStoredUrlException(String message) {
        super(message);
    }

}
