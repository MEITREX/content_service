package de.unistuttgart.iste.meitrex.content_service.service;

import de.unistuttgart.iste.meitrex.common.dapr.TopicPublisher;
import de.unistuttgart.iste.meitrex.common.event.ContentProgressedEvent;
import de.unistuttgart.iste.meitrex.common.event.ItemResponse;
import de.unistuttgart.iste.meitrex.common.event.Response;
import de.unistuttgart.iste.meitrex.common.event.UserProgressUpdatedEvent;
import de.unistuttgart.iste.meitrex.content_service.TestData;
import de.unistuttgart.iste.meitrex.content_service.persistence.entity.*;
import de.unistuttgart.iste.meitrex.content_service.persistence.mapper.ContentMapper;
import de.unistuttgart.iste.meitrex.content_service.persistence.mapper.UserProgressDataMapper;

// === added imports ===
import de.unistuttgart.iste.meitrex.content_service.persistence.mapper.StageMapper;
import de.unistuttgart.iste.meitrex.content_service.persistence.repository.ItemRepository;
import de.unistuttgart.iste.meitrex.content_service.persistence.repository.MessageSequenceNoEntityRepository;
import de.unistuttgart.iste.meitrex.content_service.persistence.repository.SectionRepository;
import de.unistuttgart.iste.meitrex.content_service.persistence.repository.StageRepository;

import de.unistuttgart.iste.meitrex.content_service.persistence.repository.UserProgressDataRepository;

import de.unistuttgart.iste.meitrex.generated.dto.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;

import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.*;
import java.util.stream.Collectors;

import static de.unistuttgart.iste.meitrex.content_service.TestData.buildDummyUserProgressData;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
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
    @Mock
    private StageService stageService;

    @InjectMocks
    private UserProgressDataService userProgressDataService;

    // === added mocks (不影响原有用例) ===
    @Mock private SectionService sectionService;
    @Mock private SectionRepository sectionRepository;
    @Mock private StageRepository stageRepository; // 未使用也没关系
    @Mock private StageMapper stageMapper;
    @Mock private ItemRepository itemRepository; // 用例里不给 responses，不会触发
    @Mock private MessageSequenceNoEntityRepository messageSequenceNoEntityRepository;

    // === added: 统一修复 logProgress() 的 NPE（不改原有测试正文）===
    @BeforeEach
    void stubSequenceRepo() {
        when(messageSequenceNoEntityRepository.save(any(MessageSequenceNoEntity.class)))
                .thenReturn(new MessageSequenceNoEntity());
    }

    /**
     * Given progress data exists for the user and content
     * When getUserProgressData is called
     * Then the progress data is returned
     */
    @Test
    void getUserProgressData() {
        final var userProgressEntity = UserProgressDataEntity.builder()
                .progressLog(Collections.emptyList())
                .learningInterval(1)
                .userId(UUID.randomUUID())
                .contentId(UUID.randomUUID())
                .build();

        doReturn(Optional.of(userProgressEntity)).when(userProgressDataRepository).findByUserIdAndContentId(any(), any());

        final var actual = userProgressDataService
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
        final var userProgressEntity = UserProgressDataEntity.builder()
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

        final var actual = userProgressDataService
                .getUserProgressData(userProgressEntity.getUserId(), userProgressEntity.getContentId());

        assertThat(actual.getUserId(), is(equalTo(userProgressEntity.getUserId())));
        assertThat(actual.getContentId(), is(equalTo(userProgressEntity.getContentId())));
        assertThat(actual.getLearningInterval(), is(equalTo(userProgressEntity.getLearningInterval())));

        final var expectedLastLearnDate = LocalDate.of(2021, 1, 1).atStartOfDay().atOffset(ZoneOffset.UTC);
        final var expectedNextLearnDate = expectedLastLearnDate.plusDays(7);
        assertThat(actual.getLastLearnDate(), is(expectedLastLearnDate));
        assertThat(actual.getNextLearnDate(), is(expectedNextLearnDate));
        assertThat(actual.getLog(), hasSize(1));

        verify(userProgressDataRepository).findByUserIdAndContentId(any(), any());
    }

    @Test
    void userDataIsInitializedWhenAbsent() {
        final var contentId = UUID.randomUUID();
        final var userId = UUID.randomUUID();
        final AssessmentEntity assessmentEntity = TestData.dummyAssessmentEntityBuilder(UUID.randomUUID())
                .assessmentMetadata(TestData.dummyAssessmentMetadataEmbeddableBuilder()
                        .initialLearningInterval(2)
                        .build())
                .id(contentId).build();

        doReturn(Optional.empty()).when(userProgressDataRepository).findByUserIdAndContentId(any(), any());
        doReturn(assessmentEntity).when(contentService).requireContentExisting(any());
        // save method returns its argument
        doAnswer(returnsFirstArg()).when(userProgressDataRepository).save(any(UserProgressDataEntity.class));

        final var actual = userProgressDataService.getUserProgressData(userId, contentId);

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
        final var contentId = UUID.randomUUID();
        final var userId = UUID.randomUUID();
        final Content content = MediaContent.builder()
                .setId(contentId)
                .setMetadata(ContentMetadata.builder()
                        .setChapterId(UUID.randomUUID())
                        .setCourseId(UUID.randomUUID())
                        .build())
                .build();
        final ContentProgressedEvent event = ContentProgressedEvent.builder()
                .contentId(contentId)
                .userId(userId)
                .timeToComplete(100)
                .correctness(1.0)
                .hintsUsed(0)
                .success(true)
                .responses(new ArrayList<>())
                .build();

        final UserProgressDataEntity initialProgress = UserProgressDataEntity.builder()
                .progressLog(new ArrayList<>())
                .learningInterval(null)
                .userId(userId)
                .contentId(contentId)
                .build();

        doReturn(List.of(content)).when(contentService).getContentsById(List.of(contentId));
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

        final UserProgressUpdatedEvent expectedUserProgressEvent = UserProgressUpdatedEvent.builder()
                .contentId(contentId)
                .chapterId(content.getMetadata().getChapterId())
                .courseId(content.getMetadata().getCourseId())
                .userId(userId)
                .timeToComplete(100)
                .correctness(1.0)
                .hintsUsed(0)
                .success(true)
                .responses(new ArrayList<ItemResponse>())
                .build();
        verify(topicPublisher).notifyUserProgressUpdated(expectedUserProgressEvent);
    }

    /**
     * Given a user progress event with 1.0 correctness and success
     * When calculateNewLearningInterval is called
     * Then the learning interval is doubled
     */
    @Test
    void learningIntervalSuccess() {
        final ContentProgressedEvent userProgressLogEvent = ContentProgressedEvent.builder()
                .correctness(1.0)
                .success(true)
                .hintsUsed(0)
                .build();

        final UserProgressDataEntity userProgressDataEntity = UserProgressDataEntity.builder()
                .learningInterval(2)
                .build();

        final var actual = userProgressDataService.calculateNewLearningInterval(userProgressLogEvent, userProgressDataEntity);
        assertThat(actual, is(4));
    }

    /**
     * Given a user progress event with 1.0 correctness and failure
     * When calculateNewLearningInterval is called
     * Then the learning interval is halved
     */
    @Test
    void learningIntervalFailure() {
        final ContentProgressedEvent userProgressLogEvent = ContentProgressedEvent.builder()
                .correctness(1.0)
                .success(false)
                .hintsUsed(0)
                .build();

        final UserProgressDataEntity userProgressDataEntity = UserProgressDataEntity.builder()
                .learningInterval(10)
                .build();

        final var actual = userProgressDataService.calculateNewLearningInterval(userProgressLogEvent, userProgressDataEntity);
        assertThat(actual, is(5));
    }

    /**
     * Given a user progress event with 0.1 correctness and failure
     * When calculateNewLearningInterval is called
     * Then the learning interval is reduced by 95%
     */
    @Test
    void learningIntervalFailureLowCorrectness() {
        final ContentProgressedEvent userProgressLogEvent = ContentProgressedEvent.builder()
                .correctness(0.1)
                .success(false)
                .hintsUsed(0)
                .build();

        final UserProgressDataEntity userProgressDataEntity = UserProgressDataEntity.builder()
                .learningInterval(100)
                .build();

        final var actual = userProgressDataService.calculateNewLearningInterval(userProgressLogEvent, userProgressDataEntity);
        assertThat(actual, is(5));
    }

    /**
     * Given a user progress event with 0.5 correctness and success
     * When calculateNewLearningInterval is called
     * Then the learning interval is increased by 50%
     */
    @Test
    void learningIntervalSuccessLowCorrectness() {
        final ContentProgressedEvent userProgressLogEvent = ContentProgressedEvent.builder()
                .correctness(0.5)
                .success(true)
                .hintsUsed(0)
                .build();

        final UserProgressDataEntity userProgressDataEntity = UserProgressDataEntity.builder()
                .learningInterval(10)
                .build();

        final var actual = userProgressDataService.calculateNewLearningInterval(userProgressLogEvent, userProgressDataEntity);
        assertThat(actual, is(15));
    }

    /**
     * Given a user progress event with 1.0 correctness and success but 1 hint used
     * When calculateNewLearningInterval is called
     * Then the learning interval is increased by 90%
     */
    @Test
    void learningIntervalHintsUsed() {
        final ContentProgressedEvent userProgressLogEvent = ContentProgressedEvent.builder()
                .correctness(1.0)
                .success(true)
                .hintsUsed(1)
                .build();

        final UserProgressDataEntity userProgressDataEntity = UserProgressDataEntity.builder()
                .learningInterval(10)
                .build();

        final var actual = userProgressDataService.calculateNewLearningInterval(userProgressLogEvent, userProgressDataEntity);
        assertThat(actual, is(19));
    }

    /**
     * Given a user progress event with 1.0 correctness and success but 100 hints used
     * When calculateNewLearningInterval is called
     * Then the learning interval is not increased
     */
    @Test
    void learningIntervalManyHintsUsed() {
        final ContentProgressedEvent userProgressLogEvent = ContentProgressedEvent.builder()
                .correctness(1.0)
                .success(true)
                .hintsUsed(100)
                .build();

        final UserProgressDataEntity userProgressDataEntity = UserProgressDataEntity.builder()
                .learningInterval(10)
                .build();

        final var actual = userProgressDataService.calculateNewLearningInterval(userProgressLogEvent, userProgressDataEntity);
        assertThat(actual, is(10));
    }

    /**
     * Test for retrieving progress data for Stages within a section.
     * In this Test scenario the UserProgress for content is also requested.
     * Therefor the expected behaviour of the method under test is to not do another database request for the individual progress data of contents within the Stage
     * but rather directly work with data made available by the provided contents in the Stage.
     */
    @Test
    void getStageProgressWithContentUserdataTests() {

        // init test data
        final UUID userId = UUID.randomUUID();
        final MediaContent mediaContent = buildDummyMediaContent();
        final MediaContent mediaContent2 = buildDummyMediaContent();

        final UserProgressDataEntity userProgressData =
                buildDummyUserProgressData(true, userId, mediaContent.getId());
        final UserProgressDataEntity userProgressData2 =
                buildDummyUserProgressData(false, userId, mediaContent2.getId());

        final Stage stage = Stage.builder()
                .setId(UUID.randomUUID())
                .setPosition(0)
                .setRequiredContents(new ArrayList<>())
                .setOptionalContents(List.of(mediaContent, mediaContent2))
                .build();

        when(userProgressDataRepository.findByUserIdAndContentId(userId, mediaContent.getId()))
                .thenReturn(Optional.of(userProgressData));
        when(userProgressDataRepository.findByUserIdAndContentId(userId, mediaContent2.getId()))
                .thenReturn(Optional.of(userProgressData2));

        // run method under test
        final double result = userProgressDataService.getStageProgressForUser(stage, userId, false);

        // verify methods called
        verify(userProgressDataRepository, times(2)).findByUserIdAndContentId(any(), any());

        // assertions
        assertEquals(50.0, result);
    }

    /**
     * Test for retrieving progress data for Stages within a section.
     * In this Test scenario the UserProgress for content is not requested.
     * Expected behaviour therefor contains the retrieval of all progress data for contents within the Stage.
     */
    @Test
    void getStageProgressWithDbQueryTests() {

        // init test data
        final UUID userId = UUID.randomUUID();
        final MediaContent mediaContent = buildDummyMediaContent();
        final MediaContent mediaContent2 = buildDummyMediaContent();

        final Stage stage = Stage.builder()
                .setId(UUID.randomUUID())
                .setPosition(0)
                .setRequiredContents(List.of(mediaContent, mediaContent2))
                .setOptionalContents(new ArrayList<>())
                .build();

        final UserProgressDataEntity progressDataEntity = buildDummyUserProgressData(true, userId, mediaContent.getId());
        final UserProgressDataEntity progressDataEntity2 = buildDummyUserProgressData(false, userId, mediaContent2.getId());

        // mock repository
        doReturn(Optional.of(progressDataEntity)).when(userProgressDataRepository).findByUserIdAndContentId(userId, mediaContent.getId());
        doReturn(Optional.of(progressDataEntity2)).when(userProgressDataRepository).findByUserIdAndContentId(userId, mediaContent2.getId());

        // run method under test
        final double result = userProgressDataService.getStageProgressForUser(stage, userId, true);

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
        final UUID chapterId1 = UUID.randomUUID();
        final UUID chapterId2 = UUID.randomUUID();
        final UUID userId = UUID.randomUUID();

        final List<UUID> chapterIds = List.of(chapterId1, chapterId2);

        // init content and user progress
        final List<MediaContentEntity> mediaContentEntities = List.of(TestData.buildContentEntity(chapterId1),
                TestData.buildContentEntity(chapterId1));

        for (int i = 0; i < mediaContentEntities.size(); i++) {
            final MediaContentEntity mediaContentEntity = mediaContentEntities.get(i);
            final boolean success = i % 2 == 0;
            final UserProgressDataEntity progressDataEntity = buildDummyUserProgressData(success, userId, mediaContentEntity.getId());
            // mock repository calls
            doReturn(Optional.of(progressDataEntity)).when(userProgressDataRepository)
                    .findByUserIdAndContentId(userId, mediaContentEntity.getId());

        }

        final List<Content> contentsForChapter1 = mediaContentEntities.stream().map(contentMapper::entityToDto).toList();
        final List<Content> contentsForChapter2 = List.of();

        // mock service with repository calls
        when(contentService.getContentsByChapterIds(chapterIds)).thenReturn(List.of(contentsForChapter1, contentsForChapter2));

        // run method under test
        final List<CompositeProgressInformation> resultList = userProgressDataService.getProgressByChapterIdsForUser(chapterIds, userId);
        // assertions
        assertEquals(2, resultList.size());

        assertEquals(50.0, resultList.get(0).getProgress());
        assertEquals(1, resultList.get(0).getCompletedContents());
        assertEquals(2, resultList.get(0).getTotalContents());

        assertEquals(100.0, resultList.get(1).getProgress());
        assertEquals(0, resultList.get(1).getCompletedContents());
        assertEquals(0, resultList.get(1).getTotalContents());

        // verify called methods
        verify(userProgressDataRepository, times(2)).findByUserIdAndContentId(any(), any());
        verify(contentService, times(1)).getContentsByChapterIds(chapterIds);
    }

    /**
     * helper method to generate some generic media content DTO
     *
     * @return media content Object
     */
    private MediaContent buildDummyMediaContent() {
        final UUID contentId = UUID.randomUUID();
        final ContentMetadata metadata = ContentMetadata.builder()
                .setChapterId(UUID.randomUUID())
                .setName("TestContent")
                .setRewardPoints(10)
                .setTagNames(new ArrayList<>())
                .setType(ContentType.MEDIA)
                .setSuggestedDate(OffsetDateTime.now())
                .build();

        return MediaContent.builder()
                .setId(contentId)
                .setMetadata(metadata)
                .build();
    }

    // ====================== added helpers for new tests ======================

    private Stage mkStage(UUID id, int pos, List<UUID> requiredIds, List<UUID> optionalIds) {
        Stage s = mock(Stage.class);
        when(s.getId()).thenReturn(id);
        when(s.getPosition()).thenReturn(pos);

        List<Content> req = requiredIds.stream().map(cid -> {
            Content c = mock(Content.class);
            when(c.getId()).thenReturn(cid);
            return c;
        }).collect(Collectors.toList());
        List<Content> opt = optionalIds.stream().map(cid -> {
            Content c = mock(Content.class);
            when(c.getId()).thenReturn(cid);
            return c;
        }).collect(Collectors.toList());

        when(s.getRequiredContents()).thenReturn(req);
        when(s.getOptionalContents()).thenReturn(opt);
        return s;
    }

    private SectionEntity mkSectionEntity(UUID id, int pos, StageEntity... stages) {
        SectionEntity e = new SectionEntity();
        e.setId(id);
        e.setPosition(pos);
        e.setStages(new LinkedHashSet<>(Arrays.asList(stages))); // 保序
        return e;
    }

    private StageEntity mkStageEntity(UUID id, int pos) {
        StageEntity e = new StageEntity();
        e.setId(id);
        e.setPosition(pos);
        return e;
    }

    // ====================== added tests ======================

    @Test
    void lastRequired_completed_nextStageInNextSection_shouldNotify() {
        UUID userId = UUID.randomUUID();
        UUID courseId = UUID.randomUUID();
        UUID A = UUID.randomUUID(); // 已完成
        UUID B = UUID.randomUUID(); // 正在完成的最后一个 required

        Stage currentStage = mkStage(UUID.randomUUID(), 2, List.of(A, B), List.of());
        Section currentSection = mock(Section.class);
        when(currentSection.getId()).thenReturn(UUID.randomUUID());
        when(currentSection.getCourseId()).thenReturn(courseId);

        StageEntity nextFirstStageEntity = mkStageEntity(UUID.randomUUID(), 1);
        Stage nextStageDto = mkStage(nextFirstStageEntity.getId(), 1, List.of(UUID.randomUUID()), List.of());

        when(stageService.findStageOfContent(B)).thenReturn(Optional.of(currentStage));
        when(sectionService.findSectionOfStage(currentStage.getId())).thenReturn(Optional.of(currentSection));

        when(sectionRepository.findByCourseIdIn(List.of(courseId))).thenReturn(List.of(
                mkSectionEntity(currentSection.getId(), 100,
                        mkStageEntity(UUID.randomUUID(), 1), mkStageEntity(currentStage.getId(), 2)),
                mkSectionEntity(UUID.randomUUID(), 200, nextFirstStageEntity)
        ));
        when(stageMapper.entityToDto(nextFirstStageEntity)).thenReturn(nextStageDto);

        doReturn(Optional.of(buildDummyUserProgressData(true,  userId, A)))
                .when(userProgressDataRepository).findByUserIdAndContentId(userId, A);
        doReturn(Optional.of(buildDummyUserProgressData(false, userId, B)))
                .when(userProgressDataRepository).findByUserIdAndContentId(userId, B);

        when(contentService.getContentsById(List.of(B))).thenReturn(List.of(
                MediaContent.builder().setId(B)
                        .setMetadata(ContentMetadata.builder()
                                .setCourseId(courseId).setChapterId(UUID.randomUUID()).build())
                        .build()
        ));
        doAnswer(returnsFirstArg()).when(userProgressDataRepository).save(any(UserProgressDataEntity.class));

        userProgressDataService.logUserProgress(ContentProgressedEvent.builder()
                .userId(userId).contentId(B).success(true).correctness(1.0).hintsUsed(0).timeToComplete(0)
                .responses(new ArrayList<>()).build());

        ArgumentCaptor<String> linkCap = ArgumentCaptor.forClass(String.class);
        verify(topicPublisher).notificationEvent(eq(courseId), eq(List.of(userId)),
                any(), linkCap.capture(), anyString(), anyString());

        assertThat(linkCap.getValue(), containsString("/courses/" + courseId + "/stages/" + nextStageDto.getId()));
    }

    @Test
    void optionalContent_completed_shouldNotNotify() {
        UUID userId = UUID.randomUUID();
        UUID courseId = UUID.randomUUID();
        UUID A_required = UUID.randomUUID();
        UUID B_optional = UUID.randomUUID();  // 可选，完成不应通知

        Stage currentStage = mkStage(UUID.randomUUID(), 1, List.of(A_required), List.of(B_optional));
        Stage nextStage    = mkStage(UUID.randomUUID(), 2, List.of(UUID.randomUUID()), List.of());
        Section section    = mock(Section.class);
        when(section.getId()).thenReturn(UUID.randomUUID());
        when(section.getCourseId()).thenReturn(courseId);

        when(stageService.findStageOfContent(B_optional)).thenReturn(Optional.of(currentStage));
        when(sectionService.findSectionOfStage(currentStage.getId())).thenReturn(Optional.of(section));
        when(sectionRepository.findByCourseIdIn(List.of(courseId))).thenReturn(List.of(
                mkSectionEntity(section.getId(), 1,
                        mkStageEntity(currentStage.getId(), 1), mkStageEntity(nextStage.getId(), 2))
        ));
        when(stageMapper.entityToDto(any(StageEntity.class))).thenReturn(nextStage);

        doReturn(Optional.of(buildDummyUserProgressData(false, userId, A_required)))
                .when(userProgressDataRepository).findByUserIdAndContentId(userId, A_required);

        when(contentService.getContentsById(List.of(B_optional))).thenReturn(List.of(
                MediaContent.builder().setId(B_optional)
                        .setMetadata(ContentMetadata.builder()
                                .setCourseId(courseId).setChapterId(UUID.randomUUID()).build())
                        .build()
        ));
        doAnswer(returnsFirstArg()).when(userProgressDataRepository).save(any(UserProgressDataEntity.class));

        userProgressDataService.logUserProgress(ContentProgressedEvent.builder()
                .userId(userId).contentId(B_optional).success(true).correctness(1.0).hintsUsed(0).timeToComplete(0)
                .responses(new ArrayList<>()).build());

        verify(topicPublisher, never()).notificationEvent(any(), any(), any(), any(), any(), any());
    }

    @Test
    void noGlobalNextStage_shouldNotNotify() {
        UUID userId = UUID.randomUUID();
        UUID courseId = UUID.randomUUID();
        UUID A = UUID.randomUUID();
        UUID B = UUID.randomUUID(); // 完成本阶段最后一个 required，但课程已无后续阶段

        Stage currentStage = mkStage(UUID.randomUUID(), 3, List.of(A, B), List.of());
        Section section    = mock(Section.class);
        when(section.getId()).thenReturn(UUID.randomUUID());
        when(section.getCourseId()).thenReturn(courseId);

        when(stageService.findStageOfContent(B)).thenReturn(Optional.of(currentStage));
        when(sectionService.findSectionOfStage(currentStage.getId())).thenReturn(Optional.of(section));
        when(sectionRepository.findByCourseIdIn(List.of(courseId))).thenReturn(List.of(
                mkSectionEntity(section.getId(), 1, mkStageEntity(currentStage.getId(), 3))
        ));

        doReturn(Optional.of(buildDummyUserProgressData(true,  userId, A)))
                .when(userProgressDataRepository).findByUserIdAndContentId(userId, A);
        doReturn(Optional.of(buildDummyUserProgressData(false, userId, B)))
                .when(userProgressDataRepository).findByUserIdAndContentId(userId, B);

        when(contentService.getContentsById(List.of(B))).thenReturn(List.of(
                MediaContent.builder().setId(B)
                        .setMetadata(ContentMetadata.builder()
                                .setCourseId(courseId).setChapterId(UUID.randomUUID()).build())
                        .build()
        ));
        doAnswer(returnsFirstArg()).when(userProgressDataRepository).save(any(UserProgressDataEntity.class));

        userProgressDataService.logUserProgress(ContentProgressedEvent.builder()
                .userId(userId).contentId(B).success(true).correctness(1.0).hintsUsed(0).timeToComplete(0)
                .responses(new ArrayList<>()).build());

        verify(topicPublisher, never()).notificationEvent(any(), any(), any(), any(), any(), any());
    }
}
