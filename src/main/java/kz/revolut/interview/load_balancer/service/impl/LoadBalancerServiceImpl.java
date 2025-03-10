package kz.revolut.interview.load_balancer.service.impl;

import kz.revolut.interview.load_balancer.model.LoadBalancingStrategy;
import kz.revolut.interview.load_balancer.model.ServerInstance;
import kz.revolut.interview.load_balancer.service.LoadBalancerService;
import kz.revolut.interview.load_balancer.strategy.selector.LoadServiceSelector;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class LoadBalancerServiceImpl implements LoadBalancerService {

    private final List<ServerInstance> servers = new CopyOnWriteArrayList<>();
    private final Lock lock = new ReentrantLock();
    private final int maxServers;

    private LoadBalancingStrategy strategy;

    public LoadBalancerServiceImpl(LoadBalancingStrategy strategy,
                                   int maxServers) {
        this.strategy = strategy;
        this.maxServers = maxServers;
    }

    @Override
    public void addServer(ServerInstance serverInstance) {
        lock.lock();
        try {
            if (servers.size() == maxServers) {
                throw new IllegalArgumentException("Servers size is full");
            }

            if (!servers.contains(serverInstance)) {
                servers.add(serverInstance);
            }
        } finally {
            lock.unlock();
        }
    }

    @Override
    public void removeServer(String serverId) {
        lock.lock();
        try {
            servers.removeIf(server -> Objects.equals(server.id(), serverId));
        } finally {
            lock.unlock();
        }
    }

    @Override
    public ServerInstance loadServer() {
        return LoadServiceSelector.loadStrategy(strategy).loadInstance(servers);
    }

    @Override
    public List<ServerInstance> loadAllServers() {
        return new ArrayList<>(servers);
    }

    @Override
    public void setStrategy(LoadBalancingStrategy newStrategy) {
        lock.lock();
        try {
            this.strategy = newStrategy;
        } finally {
            lock.unlock();
        }
    }

}