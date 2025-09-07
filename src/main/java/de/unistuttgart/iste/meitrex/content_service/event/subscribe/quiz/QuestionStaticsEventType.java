package de.unistuttgart.iste.meitrex.content_service.event.subscribe.quiz;

import lombok.Builder;
import lombok.Getter;

import java.util.UUID;

/**
 * <p>
 * This class is used by different events and other services depends on it.
 * Only make changes that are compatible. Adding fields only is allowed.
 * </p>
 */
@Builder
@Getter
public class QuestionStaticsEventType {

    private UUID id;

    private UUID questionId;

    private UUID userId;

    private boolean answeredCorrectly;


}
