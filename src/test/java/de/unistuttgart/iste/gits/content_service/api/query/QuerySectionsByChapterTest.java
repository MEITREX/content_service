package de.unistuttgart.iste.gits.content_service.api.query;

import de.unistuttgart.iste.gits.common.testutil.GraphQlApiTest;
import de.unistuttgart.iste.gits.common.testutil.TablesToDelete;
import de.unistuttgart.iste.gits.content_service.TestData;
import de.unistuttgart.iste.gits.content_service.persistence.entity.*;
import de.unistuttgart.iste.gits.content_service.persistence.mapper.SectionMapper;
import de.unistuttgart.iste.gits.content_service.persistence.repository.*;
import de.unistuttgart.iste.gits.generated.dto.Section;
import de.unistuttgart.iste.gits.generated.dto.Stage;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.graphql.test.tester.GraphQlTester;
import org.springframework.graphql.test.tester.HttpGraphQlTester;

import java.time.LocalDate;
import java.time.ZoneOffset;
import java.util.*;

import static org.assertj.core.api.Assertions.assertThat;

@GraphQlApiTest
@TablesToDelete({"stage_required_contents", "stage_optional_contents", "stage", "section", "content_tags", "user_progress_data_progress_log", "user_progress_data", "content", "tag"})
class QuerySectionsByChapterTest {

    @Autowired
    private SectionRepository sectionRepository;

    @Autowired
    private StageRepository stageRepository;

    @Autowired
    private ContentRepository contentRepository;

    @Autowired
    private UserProgressDataRepository userProgressDataRepository;

    @Autowired
    private SectionMapper sectionMapper;

    private final UUID chapterId = UUID.randomUUID();
    private final UUID chapterId2 = UUID.randomUUID();

    private List<SectionEntity> fillDatabaseWithSections() {
        SectionEntity sectionEntity = SectionEntity.builder()
                .name("Test Section")
                .position(0)
                .chapterId(chapterId)
                .stages(new HashSet<>())
                .build();
        SectionEntity sectionEntity2 = SectionEntity.builder()
                .name("Test Section2")
                .position(1)
                .chapterId(chapterId)
                .stages(new HashSet<>())
                .build();
        SectionEntity sectionEntity3 = SectionEntity.builder()
                .name("Test Section3")
                .position(0)
                .chapterId(chapterId2)
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

    private List<MediaContentEntity> fillDatabaseWithContent() {
        List<MediaContentEntity> contentEntities = new ArrayList<>();
        for (int i = 0; i < 3; i++) {
            MediaContentEntity contentEntity = TestData.dummyMediaContentEntityBuilder()
                    .metadata(TestData.dummyContentMetadataEmbeddableBuilder()
                            .chapterId(chapterId)
                            .build())
                    .build();
            contentEntity = contentRepository.save(contentEntity);
            contentEntities.add(contentEntity);
        }
        return contentEntities;
    }

    private List<UserProgressDataEntity> fillDatabaseWithUserProgress(UUID userId, List<MediaContentEntity> contentEntities) {
        UserProgressDataEntity userProgressDataEntity1 = UserProgressDataEntity.builder()
                .userId(userId)
                .contentId(contentEntities.get(0).getId())
                .learningInterval(1)
                .progressLog(List.of(
                        ProgressLogItemEmbeddable.builder()
                                .success(true)
                                .correctness(1.0)
                                .hintsUsed(0)
                                .timestamp(LocalDate.of(2021, 1, 1)
                                        .atStartOfDay()
                                        .atOffset(ZoneOffset.ofHours(1)))
                                .build(),
                        ProgressLogItemEmbeddable.builder()
                                .success(false)
                                .correctness(1.0)
                                .hintsUsed(0)
                                .timestamp(LocalDate.of(2021, 1, 2)
                                        .atStartOfDay()
                                        .atOffset(ZoneOffset.ofHours(1)))
                                .build()
                ))
                .build();

        UserProgressDataEntity userProgressDataEntity2 = UserProgressDataEntity.builder()
                .userId(userId)
                .contentId(contentEntities.get(1).getId())
                .learningInterval(1)
                .progressLog(List.of(
                        ProgressLogItemEmbeddable.builder()
                                .success(false)
                                .correctness(0.5)
                                .hintsUsed(0)
                                .timestamp(LocalDate.of(2021, 1, 1)
                                        .atStartOfDay()
                                        .atOffset(ZoneOffset.ofHours(1)))
                                .build(),
                        ProgressLogItemEmbeddable.builder()
                                .success(false)
                                .correctness(0.5)
                                .hintsUsed(0)
                                .timestamp(LocalDate.of(2021, 1, 2)
                                        .atStartOfDay()
                                        .atOffset(ZoneOffset.ofHours(1)))
                                .build()
                ))
                .build();

        UserProgressDataEntity userProgressDataEntity3 = UserProgressDataEntity.builder()
                .userId(userId)
                .contentId(contentEntities.get(2).getId())
                .learningInterval(1)
                .progressLog(List.of(
                        ProgressLogItemEmbeddable.builder()
                                .success(false)
                                .correctness(0.5)
                                .hintsUsed(0)
                                .timestamp(LocalDate.of(2021, 1, 1)
                                        .atStartOfDay()
                                        .atOffset(ZoneOffset.ofHours(1)))
                                .build(),
                        ProgressLogItemEmbeddable.builder()
                                .success(false)
                                .correctness(0.5)
                                .hintsUsed(0)
                                .timestamp(LocalDate.of(2021, 1, 2)
                                        .atStartOfDay()
                                        .atOffset(ZoneOffset.ofHours(1)))
                                .build()
                ))
                .build();

        List<UserProgressDataEntity> progressDataEntities = List.of(userProgressDataEntity1, userProgressDataEntity2, userProgressDataEntity3);

        userProgressDataRepository.saveAll(progressDataEntities);

        return progressDataEntities;
    }

    @Test
    void testQuerySectionsByChapter(GraphQlTester tester) {
        List<SectionEntity> entities = fillDatabaseWithSections();
        List<Section> entitiesMapped = entities.stream().map(sectionMapper::entityToDto).toList();

        String query = """
                query($chapterIds: [UUID!]!) {
                    sectionsByChapterIds(chapterIds: $chapterIds) {
                        id
                        chapterId
                        name
                        stages {
                            id
                            position
                            requiredContents {
                                id
                            }
                            optionalContents {
                                id
                            }
                        }
                    }
                }
                """;

        ParameterizedTypeReference<List<Section>> sectionListType = new ParameterizedTypeReference<>() {
        };

        List<List<Section>> result = tester.document(query)
                .variable("chapterIds", List.of(chapterId, chapterId2))
                .execute()
                .path("sectionsByChapterIds").entityList(sectionListType).get();

        assertThat(result.get(0)).containsExactlyInAnyOrder(entitiesMapped.get(0), entitiesMapped.get(1));
        assertThat(result.get(1)).containsExactlyInAnyOrder(entitiesMapped.get(2));
    }

    @Test
    void testQuerySectionsByChapterWithUserData(HttpGraphQlTester graphQlTester) {
        UUID userId = UUID.randomUUID();

        //init database Data
        List<SectionEntity> entities = fillDatabaseWithSections();
        List<MediaContentEntity> contentEntities = fillDatabaseWithContent();
        fillDatabaseWithUserProgress(userId, contentEntities);

        // link some content to stages
        SectionEntity section = entities.get(0);
        section.getStages().forEach(stageEntity -> {
            stageEntity.getRequiredContents().add(contentEntities.get(0));
            stageEntity.getRequiredContents().add(contentEntities.get(1));
            stageEntity.getOptionalContents().add(contentEntities.get(2));
        });
        sectionRepository.save(section);

        String currentUser = """
                {
                    "id": "%s",
                    "userName": "MyUserName",
                    "firstName": "John",
                    "lastName": "Doe",
                    "courseMemberships": []
                }
                """.formatted(userId.toString());


        String query = """
                query($chapterIds: [UUID!]!) {
                   sectionsByChapterIds(chapterIds: $chapterIds) {
                    id
                    chapterId
                    name
                    stages {
                        id
                        position
                           requiredContentsProgress
                        optionalContentsProgress    
                       }
                   }
                }
                """;

        graphQlTester.mutate()
                .header("CurrentUser", currentUser)
                .build()
                .document(query)
                .variable("chapterIds", List.of(chapterId))
                .execute()
                .path("sectionsByChapterIds[0]").entityList(Object.class).hasSize(2)

                .path("sectionsByChapterIds[0]").entityList(Section.class).containsExactly(
                        Section.builder()
                                .setId(entities.get(0).getId())
                                .setChapterId(chapterId)
                                .setName("Test Section")
                                .setStages(
                                        List.of(
                                                Stage.builder()
                                                        .setId(entities.get(0).getStages().iterator().next().getId())
                                                        .setPosition(0)
                                                        .setRequiredContentsProgress(50.0)
                                                        .setOptionalContentsProgress(0.0)
                                                        .build()
                                        )
                                )
                                .build(),
                        Section.builder()
                                .setId(entities.get(1).getId())
                                .setChapterId(chapterId)
                                .setName("Test Section2")
                                .setStages(
                                        List.of(
                                                Stage.builder()
                                                        .setId(entities.get(1).getStages().iterator().next().getId())
                                                        .setPosition(0)
                                                        .setRequiredContentsProgress(100.0)
                                                        .setOptionalContentsProgress(100.0)
                                                        .build()
                                        )
                                )
                                .build()
                );
    }
}
