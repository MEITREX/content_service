package de.unistuttgart.iste.meitrex.content_service.config;

import de.unistuttgart.iste.meitrex.common.dapr.MockTopicPublisher;
import de.unistuttgart.iste.meitrex.common.dapr.TopicPublisher;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.*;

@Configuration
@Profile("!prod")
@Slf4j
public class DevTopicPublisherConfiguration {

    @Bean
    public TopicPublisher getTopicPublisher() {
        log.warn("TopicPublisher is mocked. This is intended for development use only.");
        return new MockTopicPublisher();
    }

}