package de.unistuttgart.iste.gits.content_service.controller;

import de.unistuttgart.iste.gits.common.dapr.ResourceUpdateDTO;
import de.unistuttgart.iste.gits.content_service.service.ContentService;
import io.dapr.Topic;
import io.dapr.client.domain.CloudEvent;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

import java.util.Map;

/**
 * REST Controller Class listening to a dapr Topic.
 */
@Slf4j
@RestController
public class SubscriptionController {

    private final ContentService contentService;

    public SubscriptionController(ContentService contentService) {
        this.contentService = contentService;
    }

    @Topic(name = "resource-update", pubsubName = "gits")
    @PostMapping(path = "/course-service/resource-update-pubsub")
    public Mono<Void> updateAssociation(@RequestBody(required = false) CloudEvent<ResourceUpdateDTO> cloudEvent, @RequestHeader Map<String, String> headers){

            return Mono.fromRunnable( () -> contentService.forwardResourceUpdates(cloudEvent.getData()));
    }
}