package kz.revolut.interview.load_balancer.exceptions;

public class MaxCapacityReachedException extends RuntimeException {
    public MaxCapacityReachedException(String message) {
        super(message);
    }
}