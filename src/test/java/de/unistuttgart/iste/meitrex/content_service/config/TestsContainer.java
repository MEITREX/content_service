package de.unistuttgart.iste.meitrex.content_service.config;

import io.dapr.client.DaprClientBuilder;
import io.dapr.config.Properties;
import io.dapr.config.Property;
import io.dapr.testcontainers.Component;
import io.dapr.testcontainers.DaprContainer;
import io.dapr.testcontainers.DaprLogLevel;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.containers.Network;

import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;

@Configuration
public class TestsContainer {

    Logger logger = Logger.getLogger(TestsContainer.class.getName());

    final static int REDIS_PORT = 6379;

    @Value("${server.port}")
    int serverPort;


    @Bean
    public Network daprNetwork() {
        return Network.newNetwork();
    }

    @Bean
    public RedisTestContainer redisTestContainer(final Network daprNetwork) {
        RedisTestContainer rc =  new RedisTestContainer(daprNetwork);
        rc.start();
        return rc;
    }

    @Bean
    @Primary
    public TestEventPublisher createEventPublisher(DaprContainer daprContainer) {

        final int gPort = daprContainer.getGrpcPort();
        final int httpPort = daprContainer.getHttpPort();

        final Map<Property<?>, String> propertyOverrides = new HashMap<>();
        propertyOverrides.put(Properties.HTTP_PORT, String.valueOf(httpPort));
        propertyOverrides.put(Properties.GRPC_PORT, String.valueOf(gPort));
        propertyOverrides.put(Properties.GRPC_ENDPOINT, "localhost:" + gPort);
        propertyOverrides.put(Properties.HTTP_ENDPOINT, "http://localhost:" + httpPort);




        return new TestEventPublisher(
                new DaprClientBuilder()
                        .withPropertyOverrides(propertyOverrides)
                        .build());
    }


    @Bean
    @ServiceConnection
    public DaprContainer daprContainer(Network daprNetwork, RedisTestContainer redisTestContainer){

        Map<String, String> redisProperties = new HashMap<>();
        redisProperties.put("redisHost", "redis:" + REDIS_PORT);

        DaprContainer c = new DaprContainer("daprio/daprd:1.15.7")
                .withAppName("content_service")
                .withNetwork(daprNetwork)
                .withComponent(new Component("meitrex", "pubsub.redis", "v1", redisProperties))
                .withAppPort(serverPort)
                .withDaprLogLevel(DaprLogLevel.INFO)
                .withLogConsumer(outputFrame -> logger.info(outputFrame.getUtf8String()))
                .withAppChannelAddress("host.testcontainers.internal")
                .dependsOn(redisTestContainer);
        c.start();
        return c;
    }


    public static class RedisTestContainer extends GenericContainer<RedisTestContainer> {
        public RedisTestContainer(Network daprNetwork){
            super("redis:7.0.11");
            withExposedPorts(REDIS_PORT);
            withNetwork(daprNetwork);
            withNetworkAliases("redis");
        }
    }

}
