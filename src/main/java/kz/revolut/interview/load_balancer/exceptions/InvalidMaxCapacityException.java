package kz.revolut.interview.load_balancer.exceptions;

public class InvalidMaxCapacityException extends RuntimeException {
    public InvalidMaxCapacityException(String message) {
        super(message);
    }
}