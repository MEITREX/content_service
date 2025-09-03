package de.unistuttgart.iste.meitrex.content_service.service;

import de.unistuttgart.iste.meitrex.common.dapr.TopicPublisher;
import de.unistuttgart.iste.meitrex.common.event.*;
import de.unistuttgart.iste.meitrex.content_service.persistence.entity.*;
import de.unistuttgart.iste.meitrex.content_service.persistence.mapper.ContentMapper;
import de.unistuttgart.iste.meitrex.content_service.persistence.mapper.StageMapper;
import de.unistuttgart.iste.meitrex.content_service.persistence.mapper.UserProgressDataMapper;
import de.unistuttgart.iste.meitrex.content_service.persistence.repository.*;
import de.unistuttgart.iste.meitrex.content_service.utility.value_changed_detector.ObjectValueChangedDetector;
import de.unistuttgart.iste.meitrex.generated.dto.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.OffsetDateTime;
import java.util.*;

import static de.unistuttgart.iste.meitrex.common.util.MeitrexCollectionUtils.countAsInt;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserProgressDataService {

    private final UserProgressDataRepository userProgressDataRepository;
    private final ContentService contentService;
    private final SectionService sectionService;
    private final StageService stageService;
    private final UserProgressDataMapper userProgressDataMapper;

    private final ItemRepository itemRepository;
    private final TopicPublisher topicPublisher;
    private final SectionRepository sectionRepository;
    private final StageRepository stageRepository;
    private final MessageSequenceNoEntityRepository messageSequenceNoEntityRepository;


    private final StageMapper stageMapper;
    private final ContentMapper contentMapper;

    /**
     * Returns the user progress data for the given user and content.
     * If no progress data exists for the given user and content, it will be created.
     */
    public UserProgressData getUserProgressData(final UUID userId, final UUID contentId) {
        final UserProgressDataEntity dbProgressData = getUserProgressDataEntity(userId, contentId);

        return userProgressDataMapper.entityToDto(dbProgressData);
    }

    /**
     * Retrieves a User Progress Object for a user, content combination from the database
     *
     * @param userId    ID of user
     * @param contentId ID of content
     * @return User Progress Entity from the database
     */
    private UserProgressDataEntity getUserProgressDataEntity(final UUID userId, final UUID contentId) {
        return userProgressDataRepository
                .findByUserIdAndContentId(userId, contentId)
                .orElseGet(() -> createInitialUserProgressData(userId, contentId));
    }

    /**
     * Creates a User Progress Entity in the Database with no initial Progress tracked.
     * This method is synchronized to prevent multiple threads from creating the same entity,
     * which causes a unique constraint violation.
     *
     * @param userId    ID of user
     * @param contentId ID of Content
     * @return a newly initialized User Progress Entity
     */
    public synchronized UserProgressDataEntity createInitialUserProgressData(final UUID userId, final UUID contentId) {
        log.info("Creating initial user progress data for user {} and content {}", userId, contentId);
        final ContentEntity contentEntity = contentService.requireContentExisting(contentId);

        final Integer learningInterval = contentEntity instanceof final AssessmentEntity assessmentEntity
                ? assessmentEntity.getAssessmentMetadata().getInitialLearningInterval()
                : null;

        final UserProgressDataEntity userProgressDataEntity = UserProgressDataEntity.builder()
                .userId(userId)
                .contentId(contentId)
                .progressLog(new ArrayList<>(0))
                .learningInterval(learningInterval)
                .build();

        return userProgressDataRepository.save(userProgressDataEntity);
    }

    /**
     * Logs user progress according to the given event.
     * The learning interval of the user progress data entity will be updated.
     * A new progress log item will be added to the progress log.
     * The event will be forwarded to the topic "user-progress-updated".
     *
     * @param contentProgressedEvent the event to log
     */
    public void logUserProgress(final ContentProgressedEvent contentProgressedEvent) {
        final Content content = contentService
                .getContentsById(List.of(contentProgressedEvent.getContentId())).getFirst();

        final UserProgressDataEntity userProgressDataEntity = getUserProgressDataEntity(
                contentProgressedEvent.getUserId(), contentProgressedEvent.getContentId());

        final Optional<Stage> stage = stageService.findStageOfContent(userProgressDataEntity.getContentId());

        final StageCompletedValueChangedDetector stageCompletedDetector = new StageCompletedValueChangedDetector();
        final ChapterCompletedValueChangedDetector chapterCompletedDetector = new ChapterCompletedValueChangedDetector();
        final CourseCompletedValueChangedDetector courseCompletedDetector = new CourseCompletedValueChangedDetector();

        stageCompletedDetector.measureValue(new StageCompletedValueChangedDetector.InputParameters(
                stage, userProgressDataEntity));
        chapterCompletedDetector.measureValue(new ChapterCompletedValueChangedDetector.InputParameters(
                content.getMetadata().getChapterId(), userProgressDataEntity));
        courseCompletedDetector.measureValue(new CourseCompletedValueChangedDetector.InputParameters(
                content.getMetadata().getCourseId(), userProgressDataEntity));

        userProgressDataEntity.setLearningInterval(
                calculateNewLearningInterval(contentProgressedEvent, userProgressDataEntity));

        final var logItem = userProgressDataMapper.eventToEmbeddable(contentProgressedEvent);
        logItem.setTimestamp(OffsetDateTime.now());

        final List<ProgressLogItemEmbeddable> progressLogList = userProgressDataEntity.getProgressLog();
        progressLogList.add(logItem);

        userProgressDataRepository.save(userProgressDataEntity);


        List<ItemResponse> itemResponses = new ArrayList<>();
        if (contentProgressedEvent.getResponses() != null) {
            itemResponses = createItemResponsesList(contentProgressedEvent);
        }

        // check if stages, chapters, or courses have been completed by making progress on this content, and publish
        // events for that accordingly
        stageCompletedDetector.measureValue(new StageCompletedValueChangedDetector.InputParameters(
                stage, userProgressDataEntity));
        chapterCompletedDetector.measureValue(new ChapterCompletedValueChangedDetector.InputParameters(
                content.getMetadata().getChapterId(), userProgressDataEntity));
        courseCompletedDetector.measureValue(new CourseCompletedValueChangedDetector.InputParameters(
                content.getMetadata().getCourseId(), userProgressDataEntity));

        // note that stageCompletedDetector ensures that this content has a stage, so stage.isPresent() is always true
        // the check is just to make the linter happy
        if(stageCompletedDetector.hasValueChanged() && stage.isPresent())
            topicPublisher.notifyStageCompleted(StageCompletedEvent.builder()
                    .userId(userProgressDataEntity.getUserId())
                    .stageId(stage.get().getId())
                    .chapterId(content.getMetadata().getChapterId())
                    .courseId(content.getMetadata().getCourseId())
                    .build());

        if(chapterCompletedDetector.hasValueChanged())
            topicPublisher.notifyChapterCompleted(ChapterCompletedEvent.builder()
                    .userId(userProgressDataEntity.getUserId())
                    .chapterId(content.getMetadata().getChapterId())
                    .courseId(content.getMetadata().getCourseId())
                    .build());

        if(courseCompletedDetector.hasValueChanged())
            topicPublisher.notifyCourseCompleted(CourseCompletedEvent.builder()
                    .userId(userProgressDataEntity.getUserId())
                    .courseId(content.getMetadata().getCourseId())
                    .build());

        final int attemptCount = progressLogList.size();
        topicPublisher.notifyUserProgressUpdated(createUserProgressUpdatedEvent(contentProgressedEvent, content, itemResponses, attemptCount));
    }

    /**
     * adds the item specific information to the responses
     *
     * @param event the event from the Quiz Service
     * @return list with all responses from the event and for each response the added item information
     */
    private List<ItemResponse> createItemResponsesList(final ContentProgressedEvent event) {
        List<Response> responses = event.getResponses();
        List<ItemResponse> itemResponses = new ArrayList<ItemResponse>();
        for (Response response : responses) {
            ItemEntity item = itemRepository.findById(response.getItemId()).get();
            List<SkillEntity> skillEntities = item.getAssociatedSkills();
            List<UUID> skillIds = new ArrayList<>();
            for (SkillEntity skillEntity : skillEntities) {
                skillIds.add(skillEntity.getId());
            }
            List<BloomLevel> bloomLevelsForEvent = new ArrayList<>(item.getAssociatedBloomLevels());
            ItemResponse itemResponse = ItemResponse.builder()
                    .itemId(response.getItemId())
                    .response(response.getResponse())
                    .skillIds(skillIds)
                    .levelsOfBloomsTaxonomy(bloomLevelsForEvent)
                    .build();
            itemResponses.add(itemResponse);
        }
        return itemResponses;
    }

    private UserProgressUpdatedEvent createUserProgressUpdatedEvent(
            final ContentProgressedEvent event,
            final Content content,
            final List<ItemResponse> itemResponses,
            final int attemptCount
    ) {
        final Long sequenceNo = this.fetchNextMessageSequenceNo();


        return UserProgressUpdatedEvent.builder()
                .userId(event.getUserId())
                .contentId(event.getContentId())
                .chapterId(content.getMetadata().getChapterId())
                .courseId(content.getMetadata().getCourseId())
                .success(event.isSuccess())
                .correctness(event.getCorrectness())
                .hintsUsed(event.getHintsUsed())
                .timeToComplete(event.getTimeToComplete())
                .responses(itemResponses)
                .sequenceNo(sequenceNo)
                .attempt(attemptCount)
                .build();
    }

    /**
     * Updates the learning interval of the user progress data entity based on the
     * correctness and the hints used in the last progress log event.
     * <p>
     * Currently, the learning interval is calculated as follows:
     * <ul>
     *     <li>If the last progress log event was successful, the learning interval is
     *     multiplied by 1 + correctness. The correctness is a value between 0 and 1.
     *     If the correctness was 1, the learning interval is doubled.</li>
     *     <li>Each hint also reduced the additional learning interval by 10%</li>
     *     <li>If the last progress log event was not successful, the learning interval is
     *     halved.</li>
     * </ul>
     * <p>
     * The learning interval can never be smaller than 1, except when it was never scheduled for
     * repetition to begin with.
     */
    protected Integer calculateNewLearningInterval(final ContentProgressedEvent userProgressUpdatedEvent,
                                                   final UserProgressDataEntity userProgressDataEntity) {
        if (userProgressDataEntity.getLearningInterval() == null) {
            return null;
        }
        final double newLearningInterval;
        if (userProgressUpdatedEvent.isSuccess()) {
            final int hintsUsedCapped = Math.min(userProgressUpdatedEvent.getHintsUsed(), 10);
            newLearningInterval = userProgressDataEntity.getLearningInterval() *
                    (1 + userProgressUpdatedEvent.getCorrectness() - hintsUsedCapped * 0.1);
        } else {
            newLearningInterval = userProgressDataEntity.getLearningInterval()
                    * (0.5 * userProgressUpdatedEvent.getCorrectness());
        }

        return (int) Math.floor(Math.max(1, newLearningInterval));
    }

    /**
     * Method retrieving the progress of all content within a Stage. Progress is returned as a percentage
     *
     * @param stage           Stage DTO
     * @param userId          the User progress is being tracked
     * @param requiredContent true - consider required content, false - consider optional content
     * @return progress percentage
     */
    public double getStageProgressForUser(final Stage stage, final UUID userId, final boolean requiredContent) {

        final List<Content> contentList;

        if (requiredContent) {
            contentList = stage.getRequiredContents();
        } else {
            contentList = stage.getOptionalContents();
        }

        if (contentList.isEmpty()) {
            return 100.00;
        }

        final int numbOfCompletedContent = countNumCompletedContent(userId, contentList);

        return (double) numbOfCompletedContent / contentList.size() * 100;
    }

    /**
     * Method that calculated the progress of content for an individual user for each Chapter.
     * The returned list of CompositeProgressInformation is sorted in the same order as the chapterIds list.
     *
     * @param chapterIds list of chapters for which the progress has to be evaluated
     * @param userId     the ID of the user for whom progress is evaluated
     * @return Progress for each chapter, containing a percentage of progress, absolut number of content and completed content
     */
    public List<CompositeProgressInformation> getProgressByChapterIdsForUser(final List<UUID> chapterIds, final UUID userId) {
        final List<List<Content>> contentsByChapterIds = contentService.getContentsByChapterIds(chapterIds);

        final List<CompositeProgressInformation> chapterProgressItems = new ArrayList<>();

        for (final List<Content> contentList : contentsByChapterIds) {
            final int numCompletedContent = countNumCompletedContent(userId, contentList);

            final CompositeProgressInformation compositeProgressInformation =
                    createProgressInformation(contentList, numCompletedContent);

            chapterProgressItems.add(compositeProgressInformation);
        }

        return chapterProgressItems;
    }

    /**
     * Method that calculated the progress of content for an individual user for each Chapter.
     * The returned list of CompositeProgressInformation is sorted in the same order as the chapterIds list.
     *
     * @param chapterId  chapterId for which the progress has to be evaluated
     * @param userId     the ID of the user for whom progress is evaluated
     * @return Progress for each chapter, containing a percentage of progress, absolut number of content and completed content
     */
    public CompositeProgressInformation getProgressByChapterIdForUser(final UUID chapterId, final UUID userId) {
        final List<Content> contentsByChapterIds = contentService.getContentsByChapterId(chapterId);

        final int numCompletedContent = countNumCompletedContent(userId, contentsByChapterIds);

        return createProgressInformation(contentsByChapterIds, numCompletedContent);
    }

    public boolean isStageAvailableToBeWorkedOn(final UUID stageId, final UUID userId) {
        final Optional<Section> section = sectionService.findSectionOfStage(stageId);

        // this should never happen, but let's return true in that case
        if(section.isEmpty())
            return true;

        final Optional<Integer> thisStagePosition = section.get().getStages().stream()
                .filter(x -> x.getId() == stageId)
                .findAny()
                .map(Stage::getPosition);

        // this should never happen, but let's return true in that case
        if(thisStagePosition.isEmpty())
            return true;

        final Optional<Stage> previousStage = section.get().getStages().stream()
                .filter(stage -> stage.getPosition() < thisStagePosition.get())
                .max(Comparator.comparing(Stage::getPosition));

        // if this is the first stage in the section, obviously the user is allowed to work on it in any case
        if(previousStage.isEmpty())
            return true;

        // otherwise, check if the user has completed the required contents in the previous stage
        double progress = getStageProgressForUser(previousStage.get(), userId, true);

        return progress >= 100.0;
    }

    public boolean isContentAvailableToBeWorkedOn(final UUID contentId, final UUID userId) {
        Optional<Stage> stage = stageService.findStageOfContent(contentId);

        // if content isn't part of a stage it can always be worked on
        if(stage.isEmpty())
            return true;

        // otherwise check if the stage can be worked on by the user
        return isStageAvailableToBeWorkedOn(stage.get().getId(), userId);
    }

    public List<Content> getContentsAvailableToBeWorkedOnByUserForCourseIds(final UUID userId, List<UUID> courseIds) {
        List<SectionEntity> sectionEntities = sectionRepository.findByCourseIdIn(courseIds);

        List<Content> results = new ArrayList<>();
        for(SectionEntity sectionEntity : sectionEntities) {
            for (StageEntity stageEntity : sectionEntity.getStages().stream()
                    .sorted(Comparator.comparing(StageEntity::getPosition)).toList()) {

                // add the contents of this stage to our results list
                results.addAll(stageEntity.getRequiredContents().stream().map(contentMapper::entityToDto).toList());
                results.addAll(stageEntity.getOptionalContents().stream().map(contentMapper::entityToDto).toList());

                // if required contents of this stage haven't been completed, this is the last stage the user has
                // access to in this section
                if(getStageProgressForUser(stageMapper.entityToDto(stageEntity), userId, true) < 100.0)
                    break;
            }
        }
        return results;
    }

    private static CompositeProgressInformation createProgressInformation(final List<Content> contentList, final int numCompletedContent) {
        double progress = 100.0;

        if (!contentList.isEmpty()) {
            progress = (double) numCompletedContent / contentList.size() * 100;
        }

        return CompositeProgressInformation.builder()
                .setProgress(progress)
                .setCompletedContents(numCompletedContent)
                .setTotalContents(contentList.size())
                .build();
    }

    /**
     * function counting how many Content objects have been successfully progressed/completed
     *
     * @param userId      ID of the user progress is to be checked
     * @param contentList all content objects for which the progress has to be evaluated
     * @return number of successfully completed contents
     */
    private int countNumCompletedContent(final UUID userId, final List<Content> contentList) {

        final List<UserProgressData> userProgressDataOfContents = contentList
                .stream()
                .map(Content::getId)
                .map(contentId -> getUserProgressData(userId, contentId))
                .toList();

        return countAsInt(userProgressDataOfContents, UserProgressData::getIsLearned);
    }

    private Long fetchNextMessageSequenceNo() {
        final MessageSequenceNoEntity sequenceNoEntity = this.messageSequenceNoEntityRepository.save(new MessageSequenceNoEntity());
        return sequenceNoEntity.getSequenceNo();
    }

    private class StageCompletedValueChangedDetector
            extends ObjectValueChangedDetector<StageCompletedValueChangedDetector.InputParameters, Boolean> {

        public StageCompletedValueChangedDetector() {
            super(params -> {
                // always return false if content isn't part of a stage, because in that case the stage can't be
                // completed and the false value will never change
                if(params.stage().isEmpty())
                    return false;

                return getStageProgressForUser(
                        params.stage().get(), params.progressData().getUserId(), true) < 100;
            });
        }

        public record InputParameters(Optional<Stage> stage, UserProgressDataEntity progressData) {
        }
    }

    private class ChapterCompletedValueChangedDetector
            extends ObjectValueChangedDetector<ChapterCompletedValueChangedDetector.InputParameters, Boolean> {

        public ChapterCompletedValueChangedDetector() {
            super(params -> {

                List<Content> contentsOfChapter = contentService.getContentsByChapterId(params.chapterId());

                // if there is no content in the chapter, it is considered completed
                if(contentsOfChapter.isEmpty())
                    return true;

                CompositeProgressInformation chapterProgress =
                        getProgressByChapterIdForUser(params.chapterId(), params.progressData().getUserId());

                return chapterProgress.getProgress() < 100.0;
            });
        }

        public record InputParameters(UUID chapterId, UserProgressDataEntity progressData) {
        }
    }

    private class CourseCompletedValueChangedDetector
            extends ObjectValueChangedDetector<CourseCompletedValueChangedDetector.InputParameters, Boolean> {

        public CourseCompletedValueChangedDetector() {
            super(params -> {
                List<Content> contentsOfCourse =
                        contentService.getContentsByCourseIds(List.of(params.courseId())).stream()
                                .flatMap(Collection::stream)
                                .toList();

                return countNumCompletedContent(params.progressData().getUserId(), contentsOfCourse)
                        < contentsOfCourse.size();
            });
        }

        public record InputParameters(UUID courseId, UserProgressDataEntity progressData) {
        }
    }
}
