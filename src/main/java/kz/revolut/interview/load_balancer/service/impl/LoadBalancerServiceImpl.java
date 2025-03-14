package kz.revolut.interview.load_balancer.service.impl;

import kz.revolut.interview.load_balancer.exceptions.InvalidMaxCapacityException;
import kz.revolut.interview.load_balancer.exceptions.MaxCapacityReachedException;
import kz.revolut.interview.load_balancer.model.ServerInstance;
import kz.revolut.interview.load_balancer.service.LoadBalancerService;
import kz.revolut.interview.load_balancer.strategy.LoadServerStrategy;

import java.util.List;
import java.util.Objects;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

public class LoadBalancerServiceImpl implements LoadBalancerService {

    private final List<ServerInstance> servers = new CopyOnWriteArrayList<>();
    private final ReadWriteLock lock = new ReentrantReadWriteLock();
    private final int maxCapacity;

    private LoadServerStrategy strategy;

    public LoadBalancerServiceImpl(LoadServerStrategy strategy,
                                   int maxCapacity) {
        this.strategy = Objects.requireNonNull(strategy, "Strategy cannot be null");
        if (maxCapacity <= 0) {
            throw new InvalidMaxCapacityException("maxCapacity must be greater than 0");
        }
        this.maxCapacity = maxCapacity;
    }

    @Override
    public void addServer(ServerInstance serverInstance) {
        lock.writeLock().lock();
        try {
            if (servers.size() >= maxCapacity) {
                throw new MaxCapacityReachedException("Servers size is full");
            }
            if (!servers.contains(serverInstance)) {
                servers.add(serverInstance);
            }
        } finally {
            lock.writeLock().unlock();
        }
    }

    @Override
    public void removeServer(String serverId) {
        lock.writeLock().lock();
        try {
            servers.removeIf(server -> Objects.equals(server.id(), serverId));
        } finally {
            lock.writeLock().unlock();
        }
    }

    @Override
    public ServerInstance loadServer() {
        lock.readLock().lock();
        try {
            return strategy.loadInstance(servers);
        } finally {
            lock.readLock().unlock();
        }
    }

    @Override
    public List<ServerInstance> loadAllServers() {
        lock.readLock().lock();
        try {
            return List.copyOf(servers);
        } finally {
            lock.readLock().unlock();
        }
    }

    @Override
    public void setStrategy(LoadServerStrategy newStrategy) {
        lock.writeLock().lock();
        try {
            this.strategy = Objects.requireNonNull(newStrategy, "New strategy cannot be null");
        } finally {
            lock.writeLock().unlock();
        }
    }

}