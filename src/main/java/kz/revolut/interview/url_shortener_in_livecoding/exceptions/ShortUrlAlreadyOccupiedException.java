package kz.revolut.interview.url_shortener_in_livecoding.exceptions;

public class ShortUrlAlreadyOccupiedException extends RuntimeException{

    public ShortUrlAlreadyOccupiedException(String message) {
        super(message);
    }

}
