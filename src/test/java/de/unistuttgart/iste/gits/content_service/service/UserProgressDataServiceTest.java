package de.unistuttgart.iste.gits.content_service.service;

import de.unistuttgart.iste.gits.common.event.UserProgressLogEvent;
import de.unistuttgart.iste.gits.content_service.TestData;
import de.unistuttgart.iste.gits.content_service.dapr.TopicPublisher;
import de.unistuttgart.iste.gits.content_service.persistence.mapper.ContentMapper;
import de.unistuttgart.iste.gits.content_service.persistence.entity.*;
import de.unistuttgart.iste.gits.content_service.persistence.mapper.UserProgressDataMapper;
import de.unistuttgart.iste.gits.content_service.persistence.repository.UserProgressDataRepository;
import de.unistuttgart.iste.gits.generated.dto.*;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;

import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.*;

import static de.unistuttgart.iste.gits.content_service.TestData.buildDummyUserProgressData;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.AdditionalAnswers.returnsFirstArg;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserProgressDataServiceTest {

    @Mock
    private UserProgressDataRepository userProgressDataRepository;
    @Mock
    private ContentService contentService;
    @Spy
    private UserProgressDataMapper userProgressDataMapper = new UserProgressDataMapper(new ModelMapper());

    @Spy
    private ContentMapper contentMapper = new ContentMapper(new ModelMapper());
    @Mock
    private TopicPublisher topicPublisher;

    @InjectMocks
    private UserProgressDataService userProgressDataService;

    /**
     * Given progress data exists for the user and content
     * When getUserProgressData is called
     * Then the progress data is returned
     */
    @Test
    void getUserProgressData() {
        var userProgressEntity = UserProgressDataEntity.builder()
                .progressLog(Collections.emptyList())
                .learningInterval(1)
                .userId(UUID.randomUUID())
                .contentId(UUID.randomUUID())
                .build();

        doReturn(Optional.of(userProgressEntity)).when(userProgressDataRepository).findByUserIdAndContentId(any(), any());

        var actual = userProgressDataService
                .getUserProgressData(userProgressEntity.getUserId(), userProgressEntity.getContentId());

        assertThat(actual.getUserId(), is(equalTo(userProgressEntity.getUserId())));
        assertThat(actual.getContentId(), is(equalTo(userProgressEntity.getContentId())));
        assertThat(actual.getLearningInterval(), is(equalTo(userProgressEntity.getLearningInterval())));
        assertThat(actual.getLastLearnDate(), is(nullValue()));
        assertThat(actual.getNextLearnDate(), is(nullValue()));
        assertThat(actual.getLog(), is(empty()));

        verify(userProgressDataRepository).findByUserIdAndContentId(any(), any());
    }

    /**
     * Given progress data exists for the user and content
     * When getUserProgressData is called
     * Then the progress data is returned with the correct last and next learn date
     */
    @Test
    void lastLearnDateAndNextLearnDate() {
        var userProgressEntity = UserProgressDataEntity.builder()
                .progressLog(List.of(
                        ProgressLogItemEmbeddable.builder()
                                .timestamp(OffsetDateTime.of(2021, 1, 1, 0, 0, 0, 0, ZoneOffset.UTC))
                                .timeToComplete(100)
                                .hintsUsed(0)
                                .correctness(1)
                                .success(true)
                                .build()
                ))
                .learningInterval(7)
                .userId(UUID.randomUUID())
                .contentId(UUID.randomUUID())
                .build();

        doReturn(Optional.of(userProgressEntity)).when(userProgressDataRepository).findByUserIdAndContentId(any(), any());

        var actual = userProgressDataService
                .getUserProgressData(userProgressEntity.getUserId(), userProgressEntity.getContentId());

        assertThat(actual.getUserId(), is(equalTo(userProgressEntity.getUserId())));
        assertThat(actual.getContentId(), is(equalTo(userProgressEntity.getContentId())));
        assertThat(actual.getLearningInterval(), is(equalTo(userProgressEntity.getLearningInterval())));

        var expectedLastLearnDate = LocalDate.of(2021, 1, 1).atStartOfDay().atOffset(ZoneOffset.UTC);
        var expectedNextLearnDate = expectedLastLearnDate.plusDays(7);
        assertThat(actual.getLastLearnDate(), is(expectedLastLearnDate));
        assertThat(actual.getNextLearnDate(), is(expectedNextLearnDate));
        assertThat(actual.getLog(), hasSize(1));

        verify(userProgressDataRepository).findByUserIdAndContentId(any(), any());
    }

    @Test
    void userDataIsInitializedWhenAbsent() {
        var contentId = UUID.randomUUID();
        var userId = UUID.randomUUID();
        AssessmentEntity assessmentEntity = TestData.dummyAssessmentEntityBuilder()
                .assessmentMetadata(TestData.dummyAssessmentMetadataEmbeddableBuilder()
                        .initialLearningInterval(2)
                        .build())
                .id(contentId).build();

        doReturn(Optional.empty()).when(userProgressDataRepository).findByUserIdAndContentId(any(), any());
        doReturn(assessmentEntity).when(contentService).getContentById(any());
        // save method returns its argument
        doAnswer(returnsFirstArg()).when(userProgressDataRepository).save(any(UserProgressDataEntity.class));

        var actual = userProgressDataService.getUserProgressData(userId, contentId);

        assertThat(actual.getUserId(), is(equalTo(userId)));
        assertThat(actual.getContentId(), is(equalTo(contentId)));
        assertThat(actual.getLearningInterval(), is(2));
        assertThat(actual.getLastLearnDate(), is(nullValue()));
        assertThat(actual.getNextLearnDate(), is(nullValue()));
        assertThat(actual.getLog(), is(empty()));

        verify(userProgressDataRepository).findByUserIdAndContentId(any(), any());
        verify(userProgressDataRepository, times(1)).save(any(UserProgressDataEntity.class));
    }

    /**
     * Given a user progress event
     * When logUserProgress is called
     * Then the event is added to the progress log
     */
    @Test
    void logProgress() {
        var contentId = UUID.randomUUID();
        var userId = UUID.randomUUID();
        UserProgressLogEvent event = UserProgressLogEvent.builder()
                .contentId(contentId)
                .userId(userId)
                .timeToComplete(100)
                .correctness(1.0)
                .hintsUsed(0)
                .success(true)
                .build();

        UserProgressDataEntity initialProgress = UserProgressDataEntity.builder()
                .progressLog(new ArrayList<>())
                .learningInterval(null)
                .userId(userId)
                .contentId(contentId)
                .build();

        doReturn(Optional.of(initialProgress)).when(userProgressDataRepository).findByUserIdAndContentId(any(), any());
        doAnswer(returnsFirstArg()).when(userProgressDataRepository).save(any(UserProgressDataEntity.class));

        userProgressDataService.logUserProgress(event);

        verify(userProgressDataRepository).save(
                UserProgressDataEntity.builder()
                        .contentId(contentId)
                        .userId(userId)
                        .learningInterval(null)
                        .progressLog(List.of(
                                ProgressLogItemEmbeddable.builder()
                                        .timestamp(notNull())
                                        .timeToComplete(100)
                                        .correctness(1.0)
                                        .hintsUsed(0)
                                        .success(true)
                                        .build()))
                        .build()
        );

        verify(topicPublisher).forwardContentProgressed(event);
    }

    /**
     * Given a user progress event with 1.0 correctness and success
     * When calculateNewLearningInterval is called
     * Then the learning interval is doubled
     */
    @Test
    void learningIntervalSuccess() {
        UserProgressLogEvent userProgressLogEvent = UserProgressLogEvent.builder()
                .correctness(1.0)
                .success(true)
                .hintsUsed(0)
                .build();

        UserProgressDataEntity userProgressDataEntity = UserProgressDataEntity.builder()
                .learningInterval(2)
                .build();

        var actual = userProgressDataService.calculateNewLearningInterval(userProgressLogEvent, userProgressDataEntity);
        assertThat(actual, is(4));
    }

    /**
     * Given a user progress event with 1.0 correctness and failure
     * When calculateNewLearningInterval is called
     * Then the learning interval is halved
     */
    @Test
    void learningIntervalFailure() {
        UserProgressLogEvent userProgressLogEvent = UserProgressLogEvent.builder()
                .correctness(1.0)
                .success(false)
                .hintsUsed(0)
                .build();

        UserProgressDataEntity userProgressDataEntity = UserProgressDataEntity.builder()
                .learningInterval(10)
                .build();

        var actual = userProgressDataService.calculateNewLearningInterval(userProgressLogEvent, userProgressDataEntity);
        assertThat(actual, is(5));
    }

    /**
     * Given a user progress event with 0.1 correctness and failure
     * When calculateNewLearningInterval is called
     * Then the learning interval is reduced by 95%
     */
    @Test
    void learningIntervalFailureLowCorrectness() {
        UserProgressLogEvent userProgressLogEvent = UserProgressLogEvent.builder()
                .correctness(0.1)
                .success(false)
                .hintsUsed(0)
                .build();

        UserProgressDataEntity userProgressDataEntity = UserProgressDataEntity.builder()
                .learningInterval(100)
                .build();

        var actual = userProgressDataService.calculateNewLearningInterval(userProgressLogEvent, userProgressDataEntity);
        assertThat(actual, is(5));
    }

    /**
     * Given a user progress event with 0.5 correctness and success
     * When calculateNewLearningInterval is called
     * Then the learning interval is increased by 50%
     */
    @Test
    void learningIntervalSuccessLowCorrectness() {
        UserProgressLogEvent userProgressLogEvent = UserProgressLogEvent.builder()
                .correctness(0.5)
                .success(true)
                .hintsUsed(0)
                .build();

        UserProgressDataEntity userProgressDataEntity = UserProgressDataEntity.builder()
                .learningInterval(10)
                .build();

        var actual = userProgressDataService.calculateNewLearningInterval(userProgressLogEvent, userProgressDataEntity);
        assertThat(actual, is(15));
    }

    /**
     * Given a user progress event with 1.0 correctness and success but 1 hint used
     * When calculateNewLearningInterval is called
     * Then the learning interval is increased by 90%
     */
    @Test
    void learningIntervalHintsUsed() {
        UserProgressLogEvent userProgressLogEvent = UserProgressLogEvent.builder()
                .correctness(1.0)
                .success(true)
                .hintsUsed(1)
                .build();

        UserProgressDataEntity userProgressDataEntity = UserProgressDataEntity.builder()
                .learningInterval(10)
                .build();

        var actual = userProgressDataService.calculateNewLearningInterval(userProgressLogEvent, userProgressDataEntity);
        assertThat(actual, is(19));
    }

    /**
     * Given a user progress event with 1.0 correctness and success but 100 hints used
     * When calculateNewLearningInterval is called
     * Then the learning interval is not increased
     */
    @Test
    void learningIntervalManyHintsUsed() {
        UserProgressLogEvent userProgressLogEvent = UserProgressLogEvent.builder()
                .correctness(1.0)
                .success(true)
                .hintsUsed(100)
                .build();

        UserProgressDataEntity userProgressDataEntity = UserProgressDataEntity.builder()
                .learningInterval(10)
                .build();

        var actual = userProgressDataService.calculateNewLearningInterval(userProgressLogEvent, userProgressDataEntity);
        assertThat(actual, is(10));
    }

    /**
     * Test for retrieving progress data for Stages within a section.
     * In this Test scenario the UserProgress for content is also requested.
     * Therefor the expected behaviour of the method under test is to not do another database request for the individual progress data of contents within the Stage
     * but rather directly work with data made available by the provided contents in the Stage.
     */
    @Test
    void getStageProgressWithContentUserdataTests(){

        // init test data
        UUID userId = UUID.randomUUID();
        MediaContent mediaContent = buildDummyMediaContent();
        MediaContent mediaContent2 = buildDummyMediaContent();

        UserProgressData userProgressData  = userProgressDataMapper.entityToDto(buildDummyUserProgressData(true, userId, mediaContent.getId()));
        UserProgressData userProgressData2  = userProgressDataMapper.entityToDto(buildDummyUserProgressData(false, userId, mediaContent2.getId()));

        mediaContent.setUserProgressData(userProgressData);
        mediaContent2.setUserProgressData(userProgressData2);

        Stage stage = Stage.builder()
                .setId(UUID.randomUUID())
                .setPosition(0)
                .setRequiredContents(new ArrayList<>())
                .setOptionalContents(List.of(mediaContent, mediaContent2))
                .build();

        // run method under test
        double result = userProgressDataService.getStageProgressForUser(stage, userId, false);

        // verify methods called
        verify(userProgressDataRepository, never()).findByUserIdAndContentId(any(), any());

        // assertions
        assertEquals(50.0, result);


    }

    /**
     * Test for retrieving progress data for Stages within a section.
     * In this Test scenario the UserProgress for content is not requested.
     * Expected behaviour therefor contains the retrieval of all progress data for contents within the Stage.
     */
    @Test
    void getStageProgressWithDbQueryTests(){

        // init test data
        UUID userId = UUID.randomUUID();
        MediaContent mediaContent = buildDummyMediaContent();
        MediaContent mediaContent2 = buildDummyMediaContent();

        Stage stage = Stage.builder()
                .setId(UUID.randomUUID())
                .setPosition(0)
                .setRequiredContents(List.of(mediaContent, mediaContent2))
                .setOptionalContents(new ArrayList<>())
                .build();

        UserProgressDataEntity progressDataEntity = buildDummyUserProgressData(true, userId, mediaContent.getId());
        UserProgressDataEntity progressDataEntity2 = buildDummyUserProgressData(false, userId, mediaContent2.getId());

        // mock repository
        doReturn(Optional.of(progressDataEntity)).when(userProgressDataRepository).findByUserIdAndContentId(userId, mediaContent.getId());
        doReturn(Optional.of(progressDataEntity2)).when(userProgressDataRepository).findByUserIdAndContentId(userId, mediaContent2.getId());

        // run method under test
        double result = userProgressDataService.getStageProgressForUser(stage, userId, true);

        // verify methods called
        verify(userProgressDataRepository, never()).save(any());
        verify(userProgressDataRepository, times(2)).findByUserIdAndContentId(any(), any());

        // assertions
        assertEquals(50.0, result);
    }

    /**
     * Testcase for function to calculate progress for a user over an entire chapter.
     * This Testcase assumes Progress has already been made for all content
     */
    @Test
    void getProgressByChapterIdsForUserTest() {
        UUID chapterId = UUID.randomUUID();
        UUID chapterId1 = UUID.randomUUID();
        UUID userId = UUID.randomUUID();

        List<UUID> chapterIds = List.of(chapterId, chapterId1);

        // init content and user progress
        List<MediaContentEntity> mediaContentEntities = List.of(TestData.buildContentEntity(chapterId), TestData.buildContentEntity(chapterId));
        List<MediaContentEntity> mediaContentEntities1 = List.of(TestData.buildContentEntity(chapterId1));
        for (int i = 0; i < mediaContentEntities.size(); i++) {
            MediaContentEntity mediaContentEntity = mediaContentEntities.get(i);
            UserProgressDataEntity progressDataEntity = buildDummyUserProgressData(i % 2 == 0, userId, mediaContentEntity.getId());
            mediaContentEntity.setUserProgressData(List.of(progressDataEntity));

            // mock repository calls
            doReturn(Optional.of(progressDataEntity)).when(userProgressDataRepository).findByUserIdAndContentId(userId, mediaContentEntity.getId());

        }
        mediaContentEntities1.get(0).setUserProgressData(List.of(buildDummyUserProgressData(true, userId, mediaContentEntities1.get(0).getId())));

        // create chapter -> content Mapping
        Map<UUID, List<Content>> map = new HashMap<>();
        map.put(chapterId, mediaContentEntities.stream().map(mediaContentEntity -> contentMapper.entityToDto(mediaContentEntity)).toList());
        map.put(chapterId1, mediaContentEntities1.stream().map(mediaContentEntity -> contentMapper.entityToDto(mediaContentEntity)).toList());

        //mock service with repository calls
        doReturn(Optional.of(mediaContentEntities1.get(0).getUserProgressData().get(0))).when(userProgressDataRepository).findByUserIdAndContentId(userId, mediaContentEntities1.get(0).getId());
        doReturn(map).when(contentService).getContentEntitiesSortedByChapterId(chapterIds);

        // run method under test
        List<CompositeProgressInformation> resultList = userProgressDataService.getProgressByChapterIdsForUser(chapterIds, userId);

        // verify called methods
        verify(contentService, times(1)).getContentEntitiesSortedByChapterId(chapterIds);

        // assertions
        assertEquals(2, resultList.size());

        for (CompositeProgressInformation resultItem : resultList) {
            assertTrue(chapterIds.contains(resultItem.getChapterId()));

            if (resultItem.getChapterId().equals(chapterId)) {
                assertEquals(50.0, resultItem.getProgress());
                assertEquals(1, resultItem.getCompletedContents());
                assertEquals(2, resultItem.getTotalContents());
            } else if (resultItem.getChapterId().equals(chapterId1)) {
                assertEquals(100.0, resultItem.getProgress());
                assertEquals(1, resultItem.getCompletedContents());
                assertEquals(1, resultItem.getTotalContents());
            }
        }
    }

    /**
     * helper method to generate some generic media content DTO
     *
     * @return media content Object
     */
    private MediaContent buildDummyMediaContent() {
        UUID contentId = UUID.randomUUID();
        ContentMetadata metadata = ContentMetadata.builder()
                .setChapterId(UUID.randomUUID())
                .setName("TestContent")
                .setRewardPoints(10)
                .setTagNames(new ArrayList<>())
                .setType(ContentType.MEDIA)
                .setSuggestedDate(OffsetDateTime.now())
                .build();

        MediaContent mediaContent = MediaContent.builder()
                .setId(contentId)
                .setMetadata(metadata)
                .build();

        return mediaContent;
    }


}
