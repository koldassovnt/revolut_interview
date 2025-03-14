package kz.revolut.interview.load_balancer.strategy.impl;

import kz.revolut.interview.load_balancer.model.ServerInstance;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.RepeatedTest;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.concurrent.*;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class LoadServerRoundRobinTest {

    private LoadServerRoundRobin loadServerRoundRobin;
    private ServerInstance server1;
    private ServerInstance server2;
    private ServerInstance server3;

    @BeforeEach
    void setUp() {
        loadServerRoundRobin = new LoadServerRoundRobin();
        server1 = new ServerInstance("1", "192.168.1.1");
        server2 = new ServerInstance("2", "192.168.1.2");
        server3 = new ServerInstance("3", "192.168.1.3");
    }

    @Test
    void should_select_servers_in_round_robin_order() {
        List<ServerInstance> servers = List.of(server1, server2, server3);

        assertEquals(server1, loadServerRoundRobin.loadInstance(servers));
        assertEquals(server2, loadServerRoundRobin.loadInstance(servers));
        assertEquals(server3, loadServerRoundRobin.loadInstance(servers));
    }

    @Test
    void should_restart_from_beginning_after_last_server() {
        List<ServerInstance> servers = List.of(server1, server2, server3);

        // First round
        assertEquals(server1, loadServerRoundRobin.loadInstance(servers));
        assertEquals(server2, loadServerRoundRobin.loadInstance(servers));
        assertEquals(server3, loadServerRoundRobin.loadInstance(servers));

        // Second round (should restart)
        assertEquals(server1, loadServerRoundRobin.loadInstance(servers));
        assertEquals(server2, loadServerRoundRobin.loadInstance(servers));
        assertEquals(server3, loadServerRoundRobin.loadInstance(servers));
    }

    @Test
    void should_throw_exception_when_no_servers_are_available() {
        List<ServerInstance> emptyServers = List.of();

        Exception exception = assertThrows(IllegalStateException.class,
                () -> loadServerRoundRobin.loadInstance(emptyServers));

        assertEquals("No servers available", exception.getMessage());
    }

    @Test
    void should_work_correctly_with_single_server() {
        List<ServerInstance> servers = List.of(server1);

        // Should always return the same server
        assertEquals(server1, loadServerRoundRobin.loadInstance(servers));
        assertEquals(server1, loadServerRoundRobin.loadInstance(servers));
        assertEquals(server1, loadServerRoundRobin.loadInstance(servers));
    }

    @RepeatedTest(5)// Repeat test multiple times to catch race conditions
    void should_distribute_requests_evenly_across_servers_in_multithreading() throws InterruptedException {
        int threadCount = 50;
        List<ServerInstance> servers = Arrays.asList(server1, server2, server3);
        ConcurrentMap<String, Integer> selectionCount = new ConcurrentHashMap<>();

        try (ExecutorService executor = Executors.newFixedThreadPool(threadCount)) {

            CountDownLatch latch = new CountDownLatch(threadCount);


            for (int i = 0; i < threadCount; i++) {
                executor.submit(() -> {
                    try {
                        ServerInstance selected = loadServerRoundRobin.loadInstance(servers);
                        selectionCount.merge(selected.id(), 1, Integer::sum);
                    } finally {
                        latch.countDown();
                    }
                });
            }

            latch.await(); // Wait for all threads to finish
            executor.shutdown();
        }

        // Verify all servers have been selected at least once
        Set<String> expectedServerIds = servers.stream().map(ServerInstance::id).collect(Collectors.toSet());
        Set<String> actualServerIds = selectionCount.keySet();

        assertEquals(expectedServerIds, actualServerIds, "All servers should be used at least once");
    }


}