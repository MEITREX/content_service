package de.unistuttgart.iste.meitrex.content_service;

import de.unistuttgart.iste.meitrex.content_service.persistence.entity.*;
import de.unistuttgart.iste.meitrex.content_service.persistence.repository.SectionRepository;
import de.unistuttgart.iste.meitrex.content_service.persistence.repository.StageRepository;
import de.unistuttgart.iste.meitrex.generated.dto.*;

import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.UUID;


public class TestData {

    public static MediaContentEntity.MediaContentEntityBuilder<?, ?> dummyMediaContentEntityBuilder(final UUID courseId) {
        return MediaContentEntity.builder()
                .metadata(dummyContentMetadataEmbeddableBuilder(courseId).build());
    }

    public static AssessmentEntity.AssessmentEntityBuilder<?, ?> dummyAssessmentEntityBuilder(final UUID courseId) {
        return AssessmentEntity.builder()
                .metadata(dummyContentMetadataEmbeddableBuilder(courseId)
                        .type(ContentType.FLASHCARDS)
                        .build())
                .assessmentMetadata(dummyAssessmentMetadataEmbeddableBuilder().build());
    }

    public static AssessmentEntity.AssessmentEntityBuilder<?, ?> dummyAssessmentEntityBuilderWithItems(final UUID courseId) {
        SkillEntity skillEntity = new SkillEntity();
        skillEntity.setSkillName("Test");
        skillEntity.setSkillCategory("Test Category");
        skillEntity.setCustomSkill(true);
        ItemEntity item = new ItemEntity();
        ArrayList<SkillEntity> skills = new ArrayList<>();
        skills.add(skillEntity);
        item.setAssociatedSkills(skills);
        ArrayList<BloomLevel> levels = new ArrayList<>();
        levels.add(BloomLevel.UNDERSTAND);
        item.setAssociatedBloomLevels(levels);
        ArrayList<ItemEntity> items = new ArrayList<>();
        items.add(item);
        return AssessmentEntity.builder()
                .metadata(dummyContentMetadataEmbeddableBuilder(courseId)
                        .type(ContentType.FLASHCARDS)
                        .build())
                .items(items)
                .assessmentMetadata(dummyAssessmentMetadataEmbeddableBuilder().build());
    }

    public static AssessmentMetadataEmbeddable.AssessmentMetadataEmbeddableBuilder dummyAssessmentMetadataEmbeddableBuilder() {
        return AssessmentMetadataEmbeddable.builder()
                .skillPoints(2)
                .skillTypes(List.of(SkillType.REMEMBER))
                .initialLearningInterval(1);
    }

    public static ItemEntity dummyItemEntity() {
        SkillEntity skillEntity = new SkillEntity();
        skillEntity.setSkillName("Test");
        skillEntity.setSkillCategory("Test Category");
        skillEntity.setCustomSkill(true);
        ItemEntity item = new ItemEntity();
        ArrayList<SkillEntity> skills = new ArrayList<>();
        item.setAssociatedSkills(skills);
        ArrayList<BloomLevel> levels = new ArrayList<>();
        levels.add(BloomLevel.UNDERSTAND);
        item.setAssociatedBloomLevels(levels);
        return item;
    }

    public static ContentMetadataEmbeddable.ContentMetadataEmbeddableBuilder dummyContentMetadataEmbeddableBuilder(final UUID courseId) {
        return ContentMetadataEmbeddable.builder()
                .courseId(courseId)
                .chapterId(UUID.randomUUID())
                .name("Test Content")
                .rewardPoints(0)
                .suggestedDate(OffsetDateTime.now())
                .type(ContentType.MEDIA);
    }

    public static MediaContentEntity buildContentEntity(final UUID chapterId) {
        return MediaContentEntity.builder()
                .id(UUID.randomUUID())
                .metadata(
                        ContentMetadataEmbeddable.builder()
                                .tags(new HashSet<>())
                                .name("Test")
                                .type(ContentType.MEDIA)
                                .suggestedDate(OffsetDateTime.parse("2020-01-01T00:00:00.000Z"))
                                .rewardPoints(20)
                                .chapterId(chapterId)
                                .courseId(UUID.randomUUID())
                                .build()
                ).build();
    }

    /**
     * helper method to generate some progress data
     *
     * @param success   if evaluation of progress is a success
     * @param userId    ID of the User this Progress data belongs to
     * @param contentId ID of the Content the Progress is tracked for
     * @return database representation of a Progress data Item
     */
    public static UserProgressDataEntity buildDummyUserProgressData(final boolean success, final UUID userId, final UUID contentId) {
        final ProgressLogItemEmbeddable logItem = ProgressLogItemEmbeddable.builder()
                .correctness(70.00)
                .timestamp(OffsetDateTime.now())
                .hintsUsed(0)
                .success(success)
                .timeToComplete(null)
                .build();
        return UserProgressDataEntity.builder()
                .userId(userId)
                .contentId(contentId)
                .progressLog(List.of(logItem))
                .learningInterval(null)
                .build();
    }

    public static AssessmentEntity assessmentEntityWithSkillType(final UUID courseId, final UUID chapterId, final SkillType... skillTypes) {
        return dummyAssessmentEntityBuilder(courseId)
                .metadata(dummyContentMetadataEmbeddableBuilder(courseId)
                        .chapterId(chapterId)
                        .type(ContentType.FLASHCARDS)
                        .build())
                .assessmentMetadata(dummyAssessmentMetadataEmbeddableBuilder()
                        .skillTypes(List.of(skillTypes))
                        .build())
                .build();
    }

    public static AssessmentEntity assessmentEntityWithItems(final UUID courseId, final UUID chapterId) {
        SkillEntity skillEntity = new SkillEntity();
        skillEntity.setSkillName("Test");
        skillEntity.setSkillCategory("Test Category");
        skillEntity.setCustomSkill(true);
        ItemEntity item = new ItemEntity();
        ArrayList<SkillEntity> skills = new ArrayList<>();
        skills.add(skillEntity);
        item.setAssociatedSkills(skills);
        ArrayList<BloomLevel> levels = new ArrayList<>();
        levels.add(BloomLevel.UNDERSTAND);
        item.setAssociatedBloomLevels(levels);
        ArrayList<ItemEntity> items = new ArrayList<>();
        items.add(item);
        return dummyAssessmentEntityBuilder(courseId)
                .metadata(dummyContentMetadataEmbeddableBuilder(courseId)
                        .chapterId(chapterId)
                        .type(ContentType.FLASHCARDS)
                        .build())
                .assessmentMetadata(dummyAssessmentMetadataEmbeddableBuilder()
                        .skillTypes(List.of(SkillType.REMEMBER))
                        .build())
                .items(items)
                .build();
    }

    public static List<SectionEntity> fillDatabaseWithSections(final SectionRepository sectionRepository,
                                                               final StageRepository stageRepository,
                                                               final UUID courseId,
                                                               final UUID chapterId,
                                                               final UUID chapterId2) {
        SectionEntity sectionEntity = SectionEntity.builder()
                .name("Test Section")
                .position(0)
                .chapterId(chapterId)
                .courseId(courseId)
                .stages(new HashSet<>())
                .build();
        SectionEntity sectionEntity2 = SectionEntity.builder()
                .name("Test Section2")
                .position(1)
                .chapterId(chapterId)
                .courseId(courseId)
                .stages(new HashSet<>())
                .build();
        SectionEntity sectionEntity3 = SectionEntity.builder()
                .name("Test Section3")
                .position(0)
                .chapterId(chapterId2)
                .courseId(courseId)
                .stages(new HashSet<>())
                .build();

        sectionEntity = sectionRepository.save(sectionEntity);
        sectionEntity2 = sectionRepository.save(sectionEntity2);
        sectionEntity3 = sectionRepository.save(sectionEntity3);

        StageEntity stageEntity = StageEntity.builder()
                .sectionId(sectionEntity.getId())
                .position(0)
                .optionalContents(new HashSet<>())
                .requiredContents(new HashSet<>())
                .build();
        StageEntity stageEntity2 = StageEntity.builder()
                .sectionId(sectionEntity2.getId())
                .position(0)
                .optionalContents(new HashSet<>())
                .requiredContents(new HashSet<>())
                .build();
        stageEntity = stageRepository.save(stageEntity);
        stageEntity2 = stageRepository.save(stageEntity2);

        sectionEntity.getStages().add(stageEntity);
        sectionEntity2.getStages().add(stageEntity2);

        sectionEntity = sectionRepository.save(sectionEntity);
        sectionEntity2 = sectionRepository.save(sectionEntity2);

        return List.of(sectionEntity, sectionEntity2, sectionEntity3);
    }
}
