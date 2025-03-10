package kz.revolut.interview.rate_limiter;

import java.util.Deque;
import java.util.LinkedList;

public class RateLimiter {

    private final int maxRequests;  // Max requests per second
    private final long timeWindowMillis;  // Time window in milliseconds
    private final Deque<Long> requestTimestamps = new LinkedList<>();  // Stores timestamps of requests
    private final Object lock = new Object();  // Synchronization

    public RateLimiter(int maxRequests, int timeWindowMillis) {
        this.maxRequests = maxRequests;
        this.timeWindowMillis = timeWindowMillis;
    }

    public boolean allowRequest() {

        synchronized (lock) {

            long now = System.currentTimeMillis();

            // Remove timestamps older than time window
            while (!requestTimestamps.isEmpty() && requestTimestamps.peekFirst() < now - timeWindowMillis) {
                requestTimestamps.pollFirst();
            }

            if (requestTimestamps.size() < maxRequests) {
                requestTimestamps.addLast(now);
                return true; // Request is allowed
            } else {
                return false; // Request is denied (limit exceeded)
            }

        }

    }

    public static void main(String[] args) throws InterruptedException {
        RateLimiter limiter = new RateLimiter(5, 1000); // 5 requests per second

        for (int i = 0; i < 10; i++) {
            if (limiter.allowRequest()) {
                System.out.println("Request " + (i + 1) + " allowed at " + System.currentTimeMillis());
            } else {
                System.out.println("Request " + (i + 1) + " blocked!");
            }
            Thread.sleep(200);  // Simulate time between requests
        }
    }

}
