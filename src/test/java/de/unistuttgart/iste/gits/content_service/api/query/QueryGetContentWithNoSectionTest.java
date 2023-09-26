package de.unistuttgart.iste.gits.content_service.api.query;

import de.unistuttgart.iste.gits.common.testutil.GraphQlApiTest;
import de.unistuttgart.iste.gits.common.testutil.TablesToDelete;
import de.unistuttgart.iste.gits.content_service.TestData;
import de.unistuttgart.iste.gits.content_service.persistence.entity.MediaContentEntity;
import de.unistuttgart.iste.gits.content_service.persistence.entity.SectionEntity;
import de.unistuttgart.iste.gits.content_service.persistence.entity.StageEntity;
import de.unistuttgart.iste.gits.content_service.persistence.mapper.ContentMapper;
import de.unistuttgart.iste.gits.content_service.persistence.repository.ContentRepository;
import de.unistuttgart.iste.gits.content_service.persistence.repository.SectionRepository;
import de.unistuttgart.iste.gits.content_service.persistence.repository.StageRepository;
import de.unistuttgart.iste.gits.generated.dto.Content;
import de.unistuttgart.iste.gits.generated.dto.MediaContent;
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

    @Test
    @Transactional
    @Commit
    void getContentWithNoSectionTest(GraphQlTester tester) {

        List<MediaContentEntity> contentEntities = fillDatabaseWithContent();
        List<Content> contentList = contentEntities.subList(2, 4).stream().map(contentMapper::entityToDto).toList();
        List<Content> contentList2 = contentEntities.subList(4, 5).stream().map(contentMapper::entityToDto).toList();
        List<SectionEntity> sectionEntities = fillDatabaseWithSections(contentEntities.subList(0, 2));

        final String query = """
                query($chapterIds: [UUID!]!) {
                    _internal_noauth_contentWithNoSectionByChapterIds(chapterIds: $chapterIds){
                        id
                        metadata {
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

        ParameterizedTypeReference<List<MediaContent>> contentListType = new ParameterizedTypeReference<List<MediaContent>>() {
        };

        List<List<MediaContent>> resultList = tester.document(query)
                .variable("chapterIds", List.of(chapterId, chapterId2))
                .execute()
                .path("_internal_noauth_contentWithNoSectionByChapterIds")
                .entityList(contentListType)
                .get();

        System.out.println(resultList);
        System.out.println("DB list");
        System.out.println(contentEntities);

        assertEquals(chapterId.toString(), contentList, resultList.get(0));
        assertEquals(chapterId2.toString(), contentList2, resultList.get(1));

    }

    private List<MediaContentEntity> fillDatabaseWithContent() {
        List<MediaContentEntity> contentEntities = new ArrayList<>();
        for (int i = 0; i < 4; i++) {
            MediaContentEntity contentEntity = TestData.dummyMediaContentEntityBuilder()
                    .metadata(TestData.dummyContentMetadataEmbeddableBuilder()
                            .suggestedDate(OffsetDateTime.parse("2020-01-01T00:00:00.000Z"))
                            .chapterId(chapterId)
                            .build())
                    .build();
            contentEntity = contentRepository.save(contentEntity);
            contentEntities.add(contentEntity);
        }
        MediaContentEntity contentEntity = TestData.dummyMediaContentEntityBuilder()
                .metadata(TestData.dummyContentMetadataEmbeddableBuilder()
                        .suggestedDate(OffsetDateTime.parse("2020-01-01T00:00:00.000Z"))
                        .chapterId(chapterId2)
                        .build())
                .build();
        contentEntity = contentRepository.save(contentEntity);
        contentEntities.add(contentEntity);

        return contentEntities;
    }


    private List<SectionEntity> fillDatabaseWithSections(List<MediaContentEntity> contentEntities) {
        SectionEntity sectionEntity = SectionEntity.builder()
                .name("Test Section")
                .chapterId(chapterId)
                .stages(new HashSet<>())
                .build();
        SectionEntity sectionEntity2 = SectionEntity.builder()
                .name("Test Section2")
                .chapterId(chapterId2)
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
