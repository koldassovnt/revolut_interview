package kz.revolut.interview.load_balancer.strategy.selector;

import kz.revolut.interview.load_balancer.model.LoadBalancingStrategy;
import kz.revolut.interview.load_balancer.strategy.LoadServerStrategy;
import kz.revolut.interview.load_balancer.strategy.impl.LoadServerRandom;
import kz.revolut.interview.load_balancer.strategy.impl.LoadServerRoundRobin;

import java.util.EnumMap;

public class LoadServiceSelector {

    private static final EnumMap<LoadBalancingStrategy, LoadServerStrategy> STRATEGY_MAP =
            new EnumMap<>(LoadBalancingStrategy.class);

    static {
        STRATEGY_MAP.put(LoadBalancingStrategy.RANDOM, new LoadServerRandom());
        STRATEGY_MAP.put(LoadBalancingStrategy.ROUND_ROBIN, new LoadServerRoundRobin());
    }

    public static LoadServerStrategy loadStrategy(LoadBalancingStrategy strategy) {

        LoadServerStrategy loadServerStrategy = STRATEGY_MAP.get(strategy);

        if (loadServerStrategy == null) {
            throw new IllegalArgumentException("No strategy found for: " + strategy);
        }

        return loadServerStrategy;

    }

}