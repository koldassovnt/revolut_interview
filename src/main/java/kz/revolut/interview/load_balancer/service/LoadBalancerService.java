package kz.revolut.interview.load_balancer.service;

import kz.revolut.interview.load_balancer.model.LoadBalancingStrategy;
import kz.revolut.interview.load_balancer.model.ServerInstance;

import java.util.List;

public interface LoadBalancerService {

    void addServer(ServerInstance serverInstance);

    void removeServer(String serverId);

    ServerInstance loadServer();

    List<ServerInstance> loadAllServers();

    void setStrategy(LoadBalancingStrategy newStrategy);

}