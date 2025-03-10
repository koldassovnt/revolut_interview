package kz.revolut.interview.load_balancer.strategy.impl;

import kz.revolut.interview.load_balancer.model.ServerInstance;
import kz.revolut.interview.load_balancer.strategy.LoadServerStrategy;

import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

public class LoadServerRoundRobin implements LoadServerStrategy {

    private final AtomicInteger currentIndex = new AtomicInteger(0);

    @Override
    public ServerInstance loadInstance(List<ServerInstance> servers) {
        if (servers.isEmpty()) {
            throw new IllegalStateException("No servers available");
        }

        int index = currentIndex.getAndUpdate(i -> (i + 1) % servers.size());

        if (index >= servers.size()) {
            index = 0;
            currentIndex.set(1);
        }

        return servers.get(index);
    }

}