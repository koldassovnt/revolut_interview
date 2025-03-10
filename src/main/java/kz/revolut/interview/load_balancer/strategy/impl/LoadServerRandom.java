package kz.revolut.interview.load_balancer.strategy.impl;

import kz.revolut.interview.load_balancer.model.ServerInstance;
import kz.revolut.interview.load_balancer.strategy.LoadServerStrategy;

import java.util.List;
import java.util.Random;

public class LoadServerRandom implements LoadServerStrategy {

    private final Random random = new Random();

    @Override
    public ServerInstance loadInstance(List<ServerInstance> servers) {
        if (servers.isEmpty()) {
            throw new IllegalStateException("No servers available");
        }

        return servers.get(random.nextInt(servers.size()));
    }

}