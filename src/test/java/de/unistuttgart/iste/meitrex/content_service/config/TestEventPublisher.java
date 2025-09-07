package de.unistuttgart.iste.meitrex.content_service.config;

import de.unistuttgart.iste.meitrex.content_service.event.subscribe.quiz.UpdateQuizEvent;
import io.dapr.client.DaprClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import reactor.core.publisher.Mono;
import lombok.Generated;

public class TestEventPublisher {

    @Generated
    private static final Logger log = LoggerFactory.getLogger(TestEventPublisher.class);
    private static final String PUBSUB_NAME = "meitrex";
    private final DaprClient client;


    @Generated
    public TestEventPublisher(final DaprClient client) {
        this.client = client;
    }


    /**
     * Publishes an event that a quiz was updated.
     *
     * @param quizEvent the event containing the updated quiz data
     */
    public Mono<Void> publishUpdateQuizEvent(final UpdateQuizEvent quizEvent) {
        return this.publishEvent(quizEvent, "quiz_updated");
    }






    protected Mono<Void> publishEvent(final Object dto, final String daprTopic) {
        return this.client.publishEvent(PUBSUB_NAME, daprTopic, dto)
                .doOnSuccess((response) -> log.debug("Published message to daprTopic {}: {}", daprTopic, response))
                .doOnError((error) -> log.error("Error while publishing message to daprTopic {}: {}", daprTopic, error.getMessage()));

    }

}
