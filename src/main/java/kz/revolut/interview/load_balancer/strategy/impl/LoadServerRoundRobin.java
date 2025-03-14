package kz.revolut.interview.load_balancer.strategy.impl;

import kz.revolut.interview.load_balancer.model.ServerInstance;
import kz.revolut.interview.load_balancer.strategy.LoadServerStrategy;

import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

public class LoadServerRoundRobin implements LoadServerStrategy {

    private final AtomicInteger currentIndex = new AtomicInteger(0);
    private final ReadWriteLock lock = new ReentrantReadWriteLock();

    @Override
    public ServerInstance loadInstance(List<ServerInstance> servers) {
        lock.readLock().lock();
        try {
            if (servers.isEmpty()) {
                throw new IllegalStateException("No servers available");
            }

            int index = currentIndex.getAndUpdate(i -> (i + 1) % servers.size());
            return servers.get(index);
        } finally {
            lock.readLock().unlock();
        }
    }

}