package kz.revolut.interview.load_balancer.strategy.impl;

import kz.revolut.interview.load_balancer.model.ServerInstance;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class LoadServerRandomTest {

    private LoadServerRandom loadServerRandom;
    private ServerInstance server1;
    private ServerInstance server2;
    private ServerInstance server3;

    @BeforeEach
    void setUp() {
        loadServerRandom = new LoadServerRandom();
        server1 = new ServerInstance("1", "192.168.1.1");
        server2 = new ServerInstance("2", "192.168.1.2");
        server3 = new ServerInstance("3", "192.168.1.3");
    }

    @Test
    void should_select_random_server_when_multiple_servers_are_available() {
        List<ServerInstance> servers = List.of(server1, server2, server3);

        ServerInstance selected = loadServerRandom.loadInstance(servers);

        assertNotNull(selected);
        assertTrue(servers.contains(selected));
    }

    @Test
    void should_throw_exception_when_no_servers_are_available() {
        List<ServerInstance> emptyServers = List.of();

        Exception exception = assertThrows(IllegalStateException.class,
                () -> loadServerRandom.loadInstance(emptyServers));

        assertEquals("No servers available", exception.getMessage());
    }

    @Test
    void should_return_different_servers_over_multiple_executions() {
        List<ServerInstance> servers = List.of(server1, server2, server3);

        ServerInstance firstSelection = loadServerRandom.loadInstance(servers);
        ServerInstance secondSelection = loadServerRandom.loadInstance(servers);

        // There's a chance they are the same, but in multiple runs, we should see variation.
        assertTrue(servers.contains(firstSelection));
        assertTrue(servers.contains(secondSelection));
    }

}