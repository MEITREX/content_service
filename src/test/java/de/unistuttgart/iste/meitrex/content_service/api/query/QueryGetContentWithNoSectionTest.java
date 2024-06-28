package de.unistuttgart.iste.meitrex.content_service.api.query;

import de.unistuttgart.iste.meitrex.common.testutil.GraphQlApiTest;
import de.unistuttgart.iste.meitrex.common.testutil.InjectCurrentUserHeader;
import de.unistuttgart.iste.meitrex.common.testutil.TablesToDelete;
import de.unistuttgart.iste.meitrex.common.user_handling.LoggedInUser;
import de.unistuttgart.iste.meitrex.content_service.TestData;
import de.unistuttgart.iste.meitrex.content_service.persistence.entity.MediaContentEntity;
import de.unistuttgart.iste.meitrex.content_service.persistence.entity.SectionEntity;
import de.unistuttgart.iste.meitrex.content_service.persistence.entity.StageEntity;
import de.unistuttgart.iste.meitrex.content_service.persistence.mapper.ContentMapper;
import de.unistuttgart.iste.meitrex.content_service.persistence.repository.ContentRepository;
import de.unistuttgart.iste.meitrex.content_service.persistence.repository.SectionRepository;
import de.unistuttgart.iste.meitrex.content_service.persistence.repository.StageRepository;
import de.unistuttgart.iste.meitrex.generated.dto.Content;
import de.unistuttgart.iste.meitrex.generated.dto.MediaContent;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.graphql.test.tester.GraphQlTester;
import org.springframework.test.annotation.Commit;

import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.UUID;

import static de.unistuttgart.iste.meitrex.common.testutil.TestUsers.userWithMembershipInCourseWithId;
import static org.springframework.test.util.AssertionErrors.assertEquals;

@GraphQlApiTest
@TablesToDelete({"stage_required_contents", "stage_optional_contents", "stage", "section", "content_tags", "user_progress_data_progress_log", "user_progress_data", "content"})
class QueryGetContentWithNoSectionTest {

    @Autowired
    private ContentRepository contentRepository;

    @Autowired
    private SectionRepository sectionRepository;

    @Autowired
    private StageRepository stageRepository;

    @Autowired
    private ContentMapper contentMapper;

    private final UUID chapterId = UUID.randomUUID();
    private final UUID chapterId2 = UUID.randomUUID();
    private final UUID courseId = UUID.randomUUID();

    @InjectCurrentUserHeader
    private final LoggedInUser loggedInUser = userWithMembershipInCourseWithId(courseId, LoggedInUser.UserRoleInCourse.STUDENT);

    @Test
    @Transactional
    @Commit
    void getContentWithNoSectionTest(final GraphQlTester tester) {

        final List<MediaContentEntity> contentEntities = fillDatabaseWithContent();
        final List<Content> contentList = contentEntities.subList(2, 4).stream().map(contentMapper::entityToDto).toList();
        final List<Content> contentList2 = contentEntities.subList(4, 5).stream().map(contentMapper::entityToDto).toList();
        final List<SectionEntity> sectionEntities = fillDatabaseWithSections(contentEntities.subList(0, 2));

        final String query = """
                query($chapterIds: [UUID!]!) {
                    _internal_noauth_contentWithNoSectionByChapterIds(chapterIds: $chapterIds){
                        id
                        metadata {
                            courseId
                            name
                            type
                            suggestedDate
                            rewardPoints
                            chapterId
                            tagNames
                        }
                    }
                }
                """;

        final ParameterizedTypeReference<List<MediaContent>> contentListType = new ParameterizedTypeReference<List<MediaContent>>() {
        };

        final List<List<MediaContent>> resultList = tester.document(query)
                .variable("chapterIds", List.of(chapterId, chapterId2))
                .execute()
                .path("_internal_noauth_contentWithNoSectionByChapterIds")
                .entityList(contentListType)
                .get();

        assertEquals(chapterId.toString(), contentList, resultList.get(0));
        assertEquals(chapterId2.toString(), contentList2, resultList.get(1));

    }

    private List<MediaContentEntity> fillDatabaseWithContent() {
        final List<MediaContentEntity> contentEntities = new ArrayList<>();
        for (int i = 0; i < 4; i++) {
            MediaContentEntity contentEntity = TestData.dummyMediaContentEntityBuilder(courseId)
                    .metadata(TestData.dummyContentMetadataEmbeddableBuilder(courseId)
                            .suggestedDate(OffsetDateTime.parse("2020-01-01T00:00:00.000Z"))
                            .chapterId(chapterId)
                            .build())
                    .build();
            contentEntity = contentRepository.save(contentEntity);
            contentEntities.add(contentEntity);
        }
        MediaContentEntity contentEntity = TestData.dummyMediaContentEntityBuilder(courseId)
                .metadata(TestData.dummyContentMetadataEmbeddableBuilder(courseId)
                        .suggestedDate(OffsetDateTime.parse("2020-01-01T00:00:00.000Z"))
                        .chapterId(chapterId2)
                        .build())
                .build();
        contentEntity = contentRepository.save(contentEntity);
        contentEntities.add(contentEntity);

        return contentEntities;
    }


    private List<SectionEntity> fillDatabaseWithSections(final List<MediaContentEntity> contentEntities) {
        SectionEntity sectionEntity = SectionEntity.builder()
                .name("Test Section")
                .courseId(courseId)
                .chapterId(chapterId)
                .stages(new HashSet<>())
                .build();
        SectionEntity sectionEntity2 = SectionEntity.builder()
                .name("Test Section2")
                .chapterId(chapterId2)
                .courseId(courseId)
                .stages(new HashSet<>())
                .build();


        sectionEntity = sectionRepository.save(sectionEntity);
        sectionEntity2 = sectionRepository.save(sectionEntity2);

        StageEntity stageEntity = StageEntity.builder()
                .sectionId(sectionEntity.getId())
                .position(0)
                .optionalContents(new HashSet<>())
                .requiredContents(new HashSet<>())
                .build();
        stageEntity.getRequiredContents().add(contentEntities.get(0));
        stageEntity.getOptionalContents().add(contentEntities.get(1));

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

        return List.of(sectionEntity, sectionEntity2);
    }
}
