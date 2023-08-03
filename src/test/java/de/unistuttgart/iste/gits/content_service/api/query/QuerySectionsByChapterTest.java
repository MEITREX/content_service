package de.unistuttgart.iste.gits.content_service.api.query;

import de.unistuttgart.iste.gits.common.testutil.GraphQlApiTest;
import de.unistuttgart.iste.gits.common.testutil.TablesToDelete;
import de.unistuttgart.iste.gits.content_service.persistence.dao.StageEntity;
import de.unistuttgart.iste.gits.content_service.persistence.dao.SectionEntity;
import de.unistuttgart.iste.gits.content_service.persistence.repository.StageRepository;
import de.unistuttgart.iste.gits.content_service.persistence.repository.SectionRepository;
import de.unistuttgart.iste.gits.generated.dto.Section;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.graphql.test.tester.GraphQlTester;

import java.util.HashSet;
import java.util.List;
import java.util.UUID;
import static org.junit.jupiter.api.Assertions.*;

@GraphQlApiTest
@TablesToDelete({"stage_required_contents", "stage_optional_content", "stage" ,"section" , "content_tags", "user_progress_data", "content", "tag"})
class QuerySectionsByChapterTest {

    @Autowired
    private SectionRepository sectionRepository;

    @Autowired
    private StageRepository stageRepository;

    private List<SectionEntity> fillDatabase(){
        UUID chapterId = UUID.randomUUID();
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

        sectionEntity = sectionRepository.save(sectionEntity);
        sectionEntity2 = sectionRepository.save(sectionEntity2);

        StageEntity stageEntity = StageEntity.builder()
                .sectionId(sectionEntity.getId())
                .position(0)
                .optionalContent(new HashSet<>())
                .requiredContents(new HashSet<>())
                .build();
        StageEntity stageEntity2 = StageEntity.builder()
                .sectionId(sectionEntity2.getId())
                .position(0)
                .optionalContent(new HashSet<>())
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
    @Test
    void testQuerySectionsByChapter(GraphQlTester tester){
        List<SectionEntity> entities = fillDatabase();

        String query = """
                query($chapterId: UUID!) {
                findSectionsByChapter(id: $chapterId){
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
        List<Section> result = tester.document(query)
                .variable("chapterId", entities.get(0).getChapterId())
                .execute()
                .path("findSectionsByChapter")
                .entityList(Section.class).get();

        for (Section section: result) {
            boolean foundEntity = false;
            // completeness checks
            assertNotNull(section);
            assertNotNull(section.getId());
            assertNotNull(section.getChapterId());
            assertNotNull(section.getName());
            assertNotNull(section.getStages());
            assertFalse(section.getStages().isEmpty());

            // entity comparison
            for (SectionEntity sectionEntity : entities) {
                if (!section.getId().equals(sectionEntity.getId())){
                    continue;
                }
                foundEntity = true;
                assertEquals(sectionEntity.getChapterId(), section.getChapterId());
                assertEquals(sectionEntity.getName(), section.getName());
                assertEquals(sectionEntity.getStages().size(), section.getStages().size());
            }

            assertTrue(foundEntity, "Entity was successfully retrieved");

        }
    }
}
