package de.unistuttgart.iste.gits.content_service.api.query;

import de.unistuttgart.iste.gits.common.testutil.GraphQlApiTest;
import de.unistuttgart.iste.gits.common.testutil.TablesToDelete;
import de.unistuttgart.iste.gits.content_service.persistence.dao.SectionEntity;
import de.unistuttgart.iste.gits.content_service.persistence.dao.StageEntity;
import de.unistuttgart.iste.gits.content_service.persistence.mapper.SectionMapper;
import de.unistuttgart.iste.gits.content_service.persistence.repository.SectionRepository;
import de.unistuttgart.iste.gits.content_service.persistence.repository.StageRepository;
import de.unistuttgart.iste.gits.generated.dto.Section;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.graphql.test.tester.GraphQlTester;

import java.util.HashSet;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@GraphQlApiTest
@TablesToDelete({"stage_required_contents", "stage_optional_contents", "stage", "section", "content_tags", "user_progress_data", "content", "tag"})
class QuerySectionsByChapterTest {

    @Autowired
    private SectionRepository sectionRepository;

    @Autowired
    private StageRepository stageRepository;

    @Autowired
    private SectionMapper sectionMapper;

    private UUID chapterId = UUID.randomUUID();
    private UUID chapterId2 = UUID.randomUUID();

    private List<SectionEntity> fillDatabase(){
        SectionEntity sectionEntity = SectionEntity.builder()
                .name("Test Section")
                .chapterId(chapterId)
                .stages(new HashSet<>())
                .build();
        SectionEntity sectionEntity2 = SectionEntity.builder()
                .name("Test Section2")
                .chapterId(chapterId)
                .stages(new HashSet<>())
                .build();
        SectionEntity sectionEntity3 = SectionEntity.builder()
                .name("Test Section3")
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
    @Test
    void testQuerySectionsByChapter(GraphQlTester tester){
        List<SectionEntity> entities = fillDatabase();
        List<Section> entitiesMapped = entities.stream().map(sectionMapper::entityToDto).toList();

        String query = """
                query($chapterIds: [UUID!]!) {
                sectionsByChapterIds(chapterIds: $chapterIds){
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

        ParameterizedTypeReference<List<Section>> sectionListType = new ParameterizedTypeReference<List<Section>>() {};

        List<List<Section>> result = tester.document(query)
                .variable("chapterIds", List.of(chapterId, chapterId2))
                .execute()
                .path("sectionsByChapterIds").entityList(sectionListType).get();

        assertThat(result.get(0)).containsExactlyInAnyOrder(entitiesMapped.get(0), entitiesMapped.get(1));
        assertThat(result.get(1)).containsExactlyInAnyOrder(entitiesMapped.get(2));
    }
}
