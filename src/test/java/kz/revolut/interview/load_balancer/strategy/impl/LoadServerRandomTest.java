package kz.revolut.interview.load_balancer.strategy.impl;

import kz.revolut.interview.ParentTest;
import kz.revolut.interview.load_balancer.model.ServerInstance;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

class LoadServerRandomTest extends ParentTest {

    private LoadServerRandom loadServerRandom;

    @BeforeEach
    void init() {
        loadServerRandom = new LoadServerRandom();
    }

    @Test
    void loadInstance__emptyServers() {

        //
        //
        assertThrows(IllegalStateException.class,
                () -> loadServerRandom.loadInstance(List.of()));
        //
        //

    }

    @Test
    void loadInstance() {

        ServerInstance serverInstance = new ServerInstance(rnd(String.class), rnd(String.class));

        //
        //
        ServerInstance loadedInstance = loadServerRandom.loadInstance(List.of(serverInstance));
        //
        //

        assertThat(loadedInstance.id()).isEqualTo(serverInstance.id());
        assertThat(loadedInstance.url()).isEqualTo(serverInstance.url());

    }

}