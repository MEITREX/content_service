package de.unistuttgart.iste.meitrex.content_service.api.query;

import de.unistuttgart.iste.meitrex.common.testutil.*;
import de.unistuttgart.iste.meitrex.common.user_handling.LoggedInUser;
import de.unistuttgart.iste.meitrex.common.user_handling.LoggedInUser.UserRoleInCourse;
import de.unistuttgart.iste.meitrex.content_service.TestData;
import de.unistuttgart.iste.meitrex.content_service.persistence.entity.*;
import de.unistuttgart.iste.meitrex.content_service.persistence.mapper.SectionMapper;
import de.unistuttgart.iste.meitrex.content_service.persistence.repository.*;
import de.unistuttgart.iste.meitrex.generated.dto.Section;
import de.unistuttgart.iste.meitrex.generated.dto.Stage;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.graphql.test.tester.GraphQlTester;
import org.springframework.graphql.test.tester.HttpGraphQlTester;

import java.time.LocalDate;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.UUID;

import static de.unistuttgart.iste.meitrex.common.testutil.TestUsers.userWithMembershipInCourseWithId;
import static org.assertj.core.api.Assertions.assertThat;

@GraphQlApiTest
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
    private final UUID courseId = UUID.randomUUID();

    @InjectCurrentUserHeader
    private final LoggedInUser loggedInUser = userWithMembershipInCourseWithId(courseId, UserRoleInCourse.STUDENT);

    private final UUID userId = loggedInUser.getId();



    private List<MediaContentEntity> fillDatabaseWithContent() {
        final List<MediaContentEntity> contentEntities = new ArrayList<>();
        for (int i = 0; i < 3; i++) {
            MediaContentEntity contentEntity = TestData.dummyMediaContentEntityBuilder(courseId)
                    .metadata(TestData.dummyContentMetadataEmbeddableBuilder(courseId)
                            .chapterId(chapterId)
                            .build())
                    .build();
            contentEntity = contentRepository.save(contentEntity);
            contentEntities.add(contentEntity);
        }
        return contentEntities;
    }

    private List<UserProgressDataEntity> fillDatabaseWithUserProgress(final UUID userId, final List<MediaContentEntity> contentEntities) {
        final UserProgressDataEntity userProgressDataEntity1 = UserProgressDataEntity.builder()
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

        final UserProgressDataEntity userProgressDataEntity2 = UserProgressDataEntity.builder()
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

        final UserProgressDataEntity userProgressDataEntity3 = UserProgressDataEntity.builder()
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

        final List<UserProgressDataEntity> progressDataEntities = List.of(userProgressDataEntity1, userProgressDataEntity2, userProgressDataEntity3);

        userProgressDataRepository.saveAll(progressDataEntities);

        return progressDataEntities;
    }

    @Test
    void testQuerySectionsByChapter(final GraphQlTester tester) {
        final List<SectionEntity> entities = TestData.fillDatabaseWithSections(
                sectionRepository, stageRepository, courseId, chapterId, chapterId2);
        final List<Section> entitiesMapped = entities.stream().map(sectionMapper::entityToDto).toList();

        final String query = """
                query($chapterIds: [UUID!]!) {
                    _internal_noauth_sectionsByChapterIds(chapterIds: $chapterIds) {
                        id
                        chapterId
                        courseId
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

        final ParameterizedTypeReference<List<Section>> sectionListType = new ParameterizedTypeReference<>() {
        };

        final List<List<Section>> result = tester.document(query)
                .variable("chapterIds", List.of(chapterId, chapterId2))
                .execute()
                .path("_internal_noauth_sectionsByChapterIds").entityList(sectionListType).get();

        assertThat(result.get(0)).containsExactlyInAnyOrder(entitiesMapped.get(0), entitiesMapped.get(1));
        assertThat(result.get(1)).containsExactlyInAnyOrder(entitiesMapped.get(2));
    }

    @Test
    void testQuerySectionsByChapterWithUserData(final HttpGraphQlTester graphQlTester) {
        //init database Data
        final List<SectionEntity> entities = TestData.fillDatabaseWithSections(
                sectionRepository, stageRepository, courseId, chapterId, chapterId2);
        final List<MediaContentEntity> contentEntities = fillDatabaseWithContent();
        fillDatabaseWithUserProgress(userId, contentEntities);

        // link some content to stages
        final SectionEntity section = entities.get(0);
        section.getStages().forEach(stageEntity -> {
            stageEntity.getRequiredContents().add(contentEntities.get(0));
            stageEntity.getRequiredContents().add(contentEntities.get(1));
            stageEntity.getOptionalContents().add(contentEntities.get(2));
        });
        sectionRepository.save(section);


        final String query = """
                query($chapterIds: [UUID!]!) {
                   _internal_noauth_sectionsByChapterIds(chapterIds: $chapterIds) {
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

        graphQlTester
                .document(query)
                .variable("chapterIds", List.of(chapterId))
                .execute()
                .path("_internal_noauth_sectionsByChapterIds[0]").entityList(Object.class).hasSize(2)

                .path("_internal_noauth_sectionsByChapterIds[0]").entityList(Section.class).containsExactly(
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
