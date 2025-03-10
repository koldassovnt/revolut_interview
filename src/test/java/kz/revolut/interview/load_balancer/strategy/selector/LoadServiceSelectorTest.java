package kz.revolut.interview.load_balancer.strategy.selector;

import kz.revolut.interview.ParentTest;
import kz.revolut.interview.load_balancer.model.LoadBalancingStrategy;
import kz.revolut.interview.load_balancer.strategy.LoadServerStrategy;
import kz.revolut.interview.load_balancer.strategy.impl.LoadServerRandom;
import kz.revolut.interview.load_balancer.strategy.impl.LoadServerRoundRobin;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class LoadServiceSelectorTest extends ParentTest {

    @Test
    void testLoadStrategy_Random() {

        //
        //
        LoadServerStrategy strategy = LoadServiceSelector.loadStrategy(LoadBalancingStrategy.RANDOM);
        //
        //

        assertNotNull(strategy);
        assertInstanceOf(LoadServerRandom.class, strategy);
    }

    @Test
    void testLoadStrategy_RoundRobin() {

        //
        //
        LoadServerStrategy strategy = LoadServiceSelector.loadStrategy(LoadBalancingStrategy.ROUND_ROBIN);
        //
        //

        assertNotNull(strategy);
        assertInstanceOf(LoadServerRoundRobin.class, strategy);
    }

    @Test
    void testLoadStrategy_InvalidStrategy() {

        //
        //
        Exception exception = assertThrows(IllegalArgumentException.class,
                () -> LoadServiceSelector.loadStrategy(null));
        //
        //

        assertEquals("No strategy found for: null", exception.getMessage());

    }

}