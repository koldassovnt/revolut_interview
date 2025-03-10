package kz.revolut.interview.load_balancer.service.impl;

import kz.revolut.interview.ParentTest;
import kz.revolut.interview.load_balancer.model.LoadBalancingStrategy;
import kz.revolut.interview.load_balancer.model.ServerInstance;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

class LoadBalancerServiceImplTest extends ParentTest {

    private LoadBalancerServiceImpl loadBalancerService;

    @BeforeEach
    void setup() {
        loadBalancerService = new LoadBalancerServiceImpl(LoadBalancingStrategy.ROUND_ROBIN, 3);
    }

    @Test
    void addServer__firstServer() {

        ServerInstance serverInstance = new ServerInstance(rnd(String.class), rnd(String.class));

        //
        //
        loadBalancerService.addServer(serverInstance);
        //
        //

        List<ServerInstance> serverInstances = loadBalancerService.loadAllServers();

        assertThat(serverInstances).isNotEmpty();
        assertThat(serverInstances).hasSize(1);

        assertThat(serverInstances.getFirst().id()).isEqualTo(serverInstance.id());
        assertThat(serverInstances.getFirst().url()).isEqualTo(serverInstance.url());

    }

    @Test
    void addServer__existingServer() {

        String id = rnd(String.class);
        String url = rnd(String.class);

        ServerInstance serverInstance0 = new ServerInstance(id, url);
        ServerInstance serverInstance1 = new ServerInstance(id, url);

        //
        //
        loadBalancerService.addServer(serverInstance0);
        loadBalancerService.addServer(serverInstance1);
        //
        //

        List<ServerInstance> serverInstances = loadBalancerService.loadAllServers();

        assertThat(serverInstances).isNotEmpty();
        assertThat(serverInstances).hasSize(1);

        assertThat(serverInstances.getFirst().id()).isEqualTo(serverInstance0.id());
        assertThat(serverInstances.getFirst().url()).isEqualTo(serverInstance0.url());

    }

    @Test
    void addServer__serverListFull() {

        ServerInstance serverInstance0 = new ServerInstance(rnd(String.class), rnd(String.class));
        ServerInstance serverInstance1 = new ServerInstance(rnd(String.class), rnd(String.class));
        ServerInstance serverInstance2 = new ServerInstance(rnd(String.class), rnd(String.class));

        ServerInstance serverInstance3 = new ServerInstance(rnd(String.class), rnd(String.class));

        loadBalancerService.addServer(serverInstance0);
        loadBalancerService.addServer(serverInstance1);
        loadBalancerService.addServer(serverInstance2);

        //
        //
        assertThrows(IllegalArgumentException.class,
                () -> loadBalancerService.addServer(serverInstance3));
        //
        //

        List<ServerInstance> serverInstances = loadBalancerService.loadAllServers();

        assertThat(serverInstances).isNotEmpty();
        assertThat(serverInstances).hasSize(3);

        assertThat(serverInstances.get(0).id()).isEqualTo(serverInstance0.id());
        assertThat(serverInstances.get(0).url()).isEqualTo(serverInstance0.url());

        assertThat(serverInstances.get(1).id()).isEqualTo(serverInstance1.id());
        assertThat(serverInstances.get(1).url()).isEqualTo(serverInstance1.url());

        assertThat(serverInstances.get(2).id()).isEqualTo(serverInstance2.id());
        assertThat(serverInstances.get(2).url()).isEqualTo(serverInstance2.url());

    }

    @Test
    void removeServer__oneInstance() {

        ServerInstance serverInstance = new ServerInstance(rnd(String.class), rnd(String.class));

        loadBalancerService.addServer(serverInstance);

        //
        //
        loadBalancerService.removeServer(serverInstance.id());
        //
        //

        List<ServerInstance> serverInstances = loadBalancerService.loadAllServers();

        assertThat(serverInstances).isEmpty();

    }

    @Test
    void removeServer__notFoundInstance() {

        ServerInstance serverInstance = new ServerInstance(rnd(String.class), rnd(String.class));

        loadBalancerService.addServer(serverInstance);

        //
        //
        loadBalancerService.removeServer(serverInstance.id() + rnd(String.class));
        //
        //

        List<ServerInstance> serverInstances = loadBalancerService.loadAllServers();

        assertThat(serverInstances).isNotEmpty();
        assertThat(serverInstances).hasSize(1);

        assertThat(serverInstances.getFirst().id()).isEqualTo(serverInstance.id());
        assertThat(serverInstances.getFirst().url()).isEqualTo(serverInstance.url());

    }

    @Test
    void loadServer__roundRobin() {

        ServerInstance serverInstance0 = new ServerInstance(rnd(String.class), rnd(String.class));
        ServerInstance serverInstance1 = new ServerInstance(rnd(String.class), rnd(String.class));
        ServerInstance serverInstance2 = new ServerInstance(rnd(String.class), rnd(String.class));

        loadBalancerService.addServer(serverInstance0);
        loadBalancerService.addServer(serverInstance1);
        loadBalancerService.addServer(serverInstance2);

        //
        //
        ServerInstance first = loadBalancerService.loadServer();
        ServerInstance second = loadBalancerService.loadServer();
        loadBalancerService.loadServer();
        ServerInstance fourth = loadBalancerService.loadServer();
        ServerInstance fifth = loadBalancerService.loadServer();
        //
        //

        assertThat(first.id()).isEqualTo(fourth.id());
        assertThat(first.url()).isEqualTo(fourth.url());

        assertThat(second.id()).isEqualTo(fifth.id());
        assertThat(second.url()).isEqualTo(fifth.url());

    }

    @Test
    void loadServer__random() {

        loadBalancerService.setStrategy(LoadBalancingStrategy.RANDOM);

        ServerInstance serverInstance0 = new ServerInstance(rnd(String.class), rnd(String.class));
        ServerInstance serverInstance1 = new ServerInstance(rnd(String.class), rnd(String.class));
        ServerInstance serverInstance2 = new ServerInstance(rnd(String.class), rnd(String.class));

        loadBalancerService.addServer(serverInstance0);
        loadBalancerService.addServer(serverInstance1);
        loadBalancerService.addServer(serverInstance2);

        Set<ServerInstance> differentInstances = new HashSet<>();

        for (int i = 0; i < 10; i++) {
            differentInstances.add(loadBalancerService.loadServer());
        }

        assertThat(differentInstances).hasSizeGreaterThan(1);

    }



}