package kz.revolut.interview.load_balancer.service.impl;

import kz.revolut.interview.load_balancer.exceptions.InvalidMaxCapacityException;
import kz.revolut.interview.load_balancer.exceptions.MaxCapacityReachedException;
import kz.revolut.interview.load_balancer.model.ServerInstance;
import kz.revolut.interview.load_balancer.strategy.LoadServerStrategy;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.parallel.Execution;
import org.junit.jupiter.api.parallel.ExecutionMode;

import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class LoadBalancerServiceImplTest {

    private LoadBalancerServiceImpl loadBalancer;
    private LoadServerStrategy mockStrategy;
    private ServerInstance server1;
    private ServerInstance server2;

    @BeforeEach
    void setUp() {
        mockStrategy = mock(LoadServerStrategy.class);
        loadBalancer = new LoadBalancerServiceImpl(mockStrategy, 2);

        server1 = new ServerInstance("1", "192.168.1.1");
        server2 = new ServerInstance("2", "192.168.1.2");
    }

    @Test
    void should_throw_exception_when_max_capacity_zero() {
        assertThrows(InvalidMaxCapacityException.class, () -> new LoadBalancerServiceImpl(mockStrategy, 0));
    }

    @Test
    void should_throw_exception_when_max_capacity_negative() {
        assertThrows(InvalidMaxCapacityException.class, () -> new LoadBalancerServiceImpl(mockStrategy, -1));
    }

    @Test
    void should_add_server_when_capacity_not_reached() {
        loadBalancer.addServer(server1);
        loadBalancer.addServer(server2);

        List<ServerInstance> servers = loadBalancer.loadAllServers();
        assertEquals(2, servers.size());
        assertTrue(servers.contains(server1));
        assertTrue(servers.contains(server2));
    }

    @Test
    void should_throw_exception_when_max_capacity_is_reached() {
        loadBalancer.addServer(server1);
        loadBalancer.addServer(server2);

        ServerInstance server3 = new ServerInstance("3", "192.168.1.3");
        assertThrows(MaxCapacityReachedException.class, () -> loadBalancer.addServer(server3));
    }

    @Test
    void should_remove_server_by_id() {
        loadBalancer.addServer(server1);
        loadBalancer.addServer(server2);

        loadBalancer.removeServer(server1.id());

        List<ServerInstance> servers = loadBalancer.loadAllServers();
        assertEquals(1, servers.size());
        assertFalse(servers.contains(server1));
    }

    @Test
    void should_not_fail_when_removing_non_existent_server() {
        loadBalancer.addServer(server1);

        loadBalancer.removeServer("non-existent-id");

        List<ServerInstance> servers = loadBalancer.loadAllServers();
        assertEquals(1, servers.size());
    }

    @Test
    void should_return_selected_server_when_load_strategy_is_used() {
        loadBalancer.addServer(server1);
        loadBalancer.addServer(server2);

        when(mockStrategy.loadInstance(anyList())).thenReturn(server1);

        ServerInstance selected = loadBalancer.loadServer();
        assertNotNull(selected);
        assertEquals(server1, selected);
    }

    @Test
    void should_change_strategy_successfully() {
        LoadServerStrategy newStrategy = mock(LoadServerStrategy.class);
        loadBalancer.setStrategy(newStrategy);

        when(newStrategy.loadInstance(anyList())).thenReturn(server2);

        ServerInstance selected = loadBalancer.loadServer();
        assertNotNull(selected);
        assertEquals(server2, selected);
    }

    @Test
    @Execution(ExecutionMode.CONCURRENT)
    void should_handle_concurrent_add_remove_operations() throws InterruptedException {
        int threadCount = 10;
        CountDownLatch latch = new CountDownLatch(threadCount);
        try (ThreadPoolExecutor executor = (ThreadPoolExecutor) Executors.newFixedThreadPool(threadCount)) {

            for (int i = 0; i < threadCount; i++) {
                int finalI = i;
                executor.execute(() -> {
                    try {
                        ServerInstance server = new ServerInstance(String.valueOf(finalI), "192.168.1." + finalI);
                        loadBalancer.addServer(server);
                        loadBalancer.removeServer(server.id());
                    } finally {
                        latch.countDown();
                    }
                });
            }

            latch.await();
            assertEquals(0, loadBalancer.loadAllServers().size());

            executor.shutdown();
        }
    }
}