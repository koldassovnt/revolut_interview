package kz.revolut.interview.load_balancer.strategy.impl;

import kz.revolut.interview.ParentTest;
import kz.revolut.interview.load_balancer.model.ServerInstance;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

class LoadServerRoundRobinTest extends ParentTest {

    private LoadServerRoundRobin loadServerRoundRobin;

    @BeforeEach
    void init() {
        loadServerRoundRobin = new LoadServerRoundRobin();
    }

    @Test
    void loadInstance__emptyServers() {

        //
        //
        assertThrows(IllegalStateException.class,
                () -> loadServerRoundRobin.loadInstance(List.of()));
        //
        //

    }

    @Test
    void loadInstance__singleLoad() {

        ServerInstance serverInstance = new ServerInstance(rnd(String.class), rnd(String.class));

        //
        //
        ServerInstance loadedInstance = loadServerRoundRobin.loadInstance(List.of(serverInstance));
        //
        //

        assertThat(loadedInstance.id()).isEqualTo(serverInstance.id());
        assertThat(loadedInstance.url()).isEqualTo(serverInstance.url());

    }

    @Test
    void loadInstance__multipleLoad() {

        ServerInstance serverInstance0 = new ServerInstance(rnd(String.class), rnd(String.class));
        ServerInstance serverInstance1 = new ServerInstance(rnd(String.class), rnd(String.class));

        List<ServerInstance> servers = List.of(serverInstance0, serverInstance1);

        //
        //
        ServerInstance first = loadServerRoundRobin.loadInstance(servers);
        ServerInstance second = loadServerRoundRobin.loadInstance(servers);
        ServerInstance third = loadServerRoundRobin.loadInstance(servers);
        ServerInstance fourth = loadServerRoundRobin.loadInstance(servers);
        //
        //

        assertThat(first.id()).isEqualTo(third.id());
        assertThat(first.url()).isEqualTo(third.url());

        assertThat(second.id()).isEqualTo(fourth.id());
        assertThat(second.url()).isEqualTo(fourth.url());

    }

}