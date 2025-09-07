package de.unistuttgart.iste.meitrex.content_service.service;

import de.unistuttgart.iste.meitrex.common.dapr.TopicPublisher;
import de.unistuttgart.iste.meitrex.common.event.*;
import de.unistuttgart.iste.meitrex.content_service.TestData;
import de.unistuttgart.iste.meitrex.content_service.persistence.entity.*;
import de.unistuttgart.iste.meitrex.content_service.persistence.repository.ContentRepository;
import de.unistuttgart.iste.meitrex.content_service.persistence.repository.StageRepository;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.util.ReflectionTestUtils;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.util.*;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@Testcontainers
@ExtendWith(MockitoExtension.class)
@SpringBootTest({"spring.main.allow-bean-definition-overriding=true"})
@Transactional
class UserProgressDataServiceEventTest {
    @Autowired
    private UserProgressDataService userProgressDataService;
    @Autowired
    private ContentRepository contentRepository;
    @Autowired
    private StageRepository stageRepository;
    @Mock
    private TopicPublisher topicPublisher;

    @BeforeEach
    public void setup() {
        // replace the autowired topic publisher with our mock
        ReflectionTestUtils.setField(userProgressDataService, "topicPublisher", topicPublisher);
    }

    /**
     * Given a user progress event
     * When logUserProgress is called
     * Then the event is added to the progress log
     */
    @Test
    void logProgress() {
        final UUID chapterId = UUID.randomUUID();
        final UUID userId = UUID.randomUUID();
        final UUID courseId = UUID.randomUUID();

        ContentEntity contentEntity = TestData.buildContentEntity(courseId, chapterId);
        contentEntity = contentRepository.save(contentEntity);

        StageEntity stageEntity = StageEntity.builder()
                .sectionId(UUID.randomUUID())
                .position(0)
                .optionalContents(new HashSet<>())
                .requiredContents(Set.of(contentEntity))
                .build();
        stageEntity = stageRepository.save(stageEntity);

        userProgressDataService.logUserProgress(ContentProgressedEvent.builder()
                .contentId(contentEntity.getId())
                .userId(userId)
                .correctness(1)
                .hintsUsed(0)
                .success(true)
                .build());

        verify(topicPublisher).notifyStageCompleted(StageCompletedEvent.builder()
                .userId(userId)
                .stageId(stageEntity.getId())
                .chapterId(chapterId)
                .courseId(courseId)
                .build());
        verify(topicPublisher).notifyChapterCompleted(ChapterCompletedEvent.builder()
                .userId(userId)
                .chapterId(chapterId)
                .courseId(courseId)
                .build());
        verify(topicPublisher).notifyCourseCompleted(CourseCompletedEvent.builder()
                .userId(userId)
                .courseId(courseId)
                .build());
    }

    /**
     * Given a user progress event
     * When logUserProgress is called and the stage/chapter/course has more content,
     * Then no StageCompletedEvent, ChapterCompletedEvent or CourseCompletedEvent are sent
     */
    @Test
    void logProgressDoesNotCompleteStageChapterOrCourseIfMoreContentRemaining() {
        final UUID chapterId = UUID.randomUUID();
        final UUID userId = UUID.randomUUID();
        final UUID courseId = UUID.randomUUID();

        // first content (user progresses this one)
        ContentEntity progressedContent = TestData.buildContentEntity(courseId, chapterId);
        progressedContent = contentRepository.save(progressedContent);

        // second content (still incomplete)
        ContentEntity otherContent = TestData.buildContentEntity(courseId, chapterId);
        otherContent = contentRepository.save(otherContent);

        StageEntity stageEntity = StageEntity.builder()
                .sectionId(UUID.randomUUID())
                .position(0)
                .optionalContents(new HashSet<>())
                .requiredContents(Set.of(progressedContent, otherContent))
                .build();
        stageEntity = stageRepository.save(stageEntity);

        // log progress only for the first content
        userProgressDataService.logUserProgress(ContentProgressedEvent.builder()
                .contentId(progressedContent.getId())
                .userId(userId)
                .correctness(1)
                .hintsUsed(0)
                .success(true)
                .build());

        // verify no completion events were sent
        verify(topicPublisher).notifyUserProgressUpdated(any(UserProgressUpdatedEvent.class));
        verify(topicPublisher, never()).notifyStageCompleted(any());
        verify(topicPublisher, never()).notifyChapterCompleted(any());
        verify(topicPublisher, never()).notifyCourseCompleted(any());
    }
}
