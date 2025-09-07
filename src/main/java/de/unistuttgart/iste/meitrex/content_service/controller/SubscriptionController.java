package de.unistuttgart.iste.meitrex.content_service.controller;


import de.unistuttgart.iste.meitrex.common.event.ChapterChangeEvent;
import de.unistuttgart.iste.meitrex.common.event.ContentProgressedEvent;
import de.unistuttgart.iste.meitrex.content_service.event.subscribe.quiz.UpdateQuizEvent;
import de.unistuttgart.iste.meitrex.content_service.persistence.entity.ContentEntity;
import de.unistuttgart.iste.meitrex.content_service.service.*;
import de.unistuttgart.iste.meitrex.common.event.CrudOperation;
import de.unistuttgart.iste.meitrex.common.event.ItemChangeEvent;
import de.unistuttgart.iste.meitrex.generated.dto.*;
import io.dapr.Topic;
import io.dapr.client.domain.CloudEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.Optional;

/**
 * REST Controller Class listening to a dapr Topic.
 */
@Slf4j
@RestController
@RequiredArgsConstructor
public class SubscriptionController {

    private final ContentService contentService;
    private final SectionService sectionService;
    private final UserProgressDataService userProgressDataService;

    /**
     * Listens to the content-progressed topic and logs the user progress.
     */
    @Topic(name = "content-progressed", pubsubName = "meitrex")
    @PostMapping(path = "/content-progressed-pubsub")
    public Mono<Void> logUserProgress(@RequestBody final CloudEvent<ContentProgressedEvent> cloudEvent) {
        return Mono.fromRunnable(() -> {
            try {
                log.info("Received content-progressed event: {}", cloudEvent.getData());
                userProgressDataService.logUserProgress(cloudEvent.getData());
            } catch (final Exception e) {
                log.error("Error while processing logUserProgress event. {}", e.getMessage());
            }
        });
    }

    @Topic(name = "chapter-changed", pubsubName = "meitrex")
    @PostMapping(path = "/content-service/chapter-changed-pubsub")
    public Mono<Void> cascadeCourseDeletion(@RequestBody final CloudEvent<ChapterChangeEvent> cloudEvent) {
        return Mono.fromRunnable(() -> {
            try {
                // Delete content associated with the chapter
                sectionService.cascadeSectionDeletion(cloudEvent.getData());
            } catch (final Exception e) {
                log.error("Error while processing chapter-changes event. {}", e.getMessage());
            }
            try {
                // Delete section
                contentService.cascadeContentDeletion(cloudEvent.getData());
            } catch (final Exception e) {
                log.error("Error while processing chapter-changes event. {}", e.getMessage());
            }

        });
    }

    @Topic(name = "item-changed", pubsubName = "meitrex")
    @PostMapping(path = "/content-service/item-changed-pubsub")
    public Mono<Void> onItemChanged(@RequestBody final CloudEvent<ItemChangeEvent> cloudEvent) {
        return Mono.fromRunnable(() -> {
            try {
                if (cloudEvent.getData().getOperation() != CrudOperation.DELETE)
                    return;
                contentService.deleteItem(cloudEvent.getData().getItemId());
            } catch (final Exception e) {
                // we need to catch all exceptions because otherwise if some invalid data is in the message queue
                // it will never get processed and instead the service will just crash forever
                log.error("Error while processing item change event", e);
            }
        });
    }

    // Quiz Events
    @Topic(name = "quiz_updated", pubsubName = "meitrex")
    @PostMapping(path = "/content-service/quiz-updated-pubsub")
    public Mono<Void> onQuizUpdated(@RequestBody final CloudEvent<UpdateQuizEvent> cloudEvent) {
        return Mono.fromRunnable(() -> {
            try {
                log.info("Received quiz update event: {}", cloudEvent.getData());
                UpdateQuizEvent event = cloudEvent.getData();
                UpdateAssessmentInput input = new UpdateAssessmentInput();
                ContentEntity content = contentService.requireContentExisting(event.getAssessmentId());
                List<ItemInput> items = Optional.ofNullable(event.getQuestionPool()).orElse(List.of()).stream().map(q -> {
                    ItemInput item = new ItemInput();
                    log.debug("received item with id {}", q.getItemId());
                    item.setId(q.getItemId());
                    return item;
                }).toList();
                input.setItems(items);
                UpdateContentMetadataInput metadata = new UpdateContentMetadataInput();
                metadata.setTagNames(content.getMetadata().getTags().stream().toList());
                metadata.setChapterId(content.getMetadata().getChapterId());
                metadata.setName(content.getMetadata().getName());
                metadata.setRewardPoints(content.getMetadata().getRewardPoints());
                metadata.setSuggestedDate(content.getMetadata().getSuggestedDate());
                input.setMetadata(metadata);
                Assessment a = contentService.updateAssessment(event.getAssessmentId(), input);
                log.info("Updated assessment: {}", a);

            } catch (final Exception e) {
                log.error("Error while processing quiz update event", e);
            }
        });
    }


}
