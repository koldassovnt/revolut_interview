package kz.revolut.interview.load_balancer.strategy;

import kz.revolut.interview.load_balancer.model.ServerInstance;

import java.util.List;

public interface LoadServerStrategy {

    ServerInstance loadInstance(List<ServerInstance> servers);

}
