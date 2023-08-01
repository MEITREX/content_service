package de.unistuttgart.iste.gits.content_service.api.query;

import de.unistuttgart.iste.gits.common.testutil.GraphQlApiTest;
import de.unistuttgart.iste.gits.content_service.persistence.dao.StageEntity;
import de.unistuttgart.iste.gits.content_service.persistence.dao.WorkPathEntity;
import de.unistuttgart.iste.gits.content_service.persistence.repository.StageRepository;
import de.unistuttgart.iste.gits.content_service.persistence.repository.WorkPathRepository;
import de.unistuttgart.iste.gits.generated.dto.WorkPath;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.graphql.test.tester.GraphQlTester;

import java.util.HashSet;
import java.util.List;
import java.util.UUID;
import static org.junit.jupiter.api.Assertions.*;

@GraphQlApiTest
class QueryWorkPathsByChapterTest {

    @Autowired
    private WorkPathRepository workPathRepository;

    @Autowired
    private StageRepository stageRepository;

    private List<WorkPathEntity> fillDatabase(){
        UUID chapterId = UUID.randomUUID();
        WorkPathEntity workPathEntity = WorkPathEntity.builder()
                .name("Test Work-Path")
                .chapterId(chapterId)
                .stages(new HashSet<>())
                .build();
        WorkPathEntity workPathEntity2 = WorkPathEntity.builder()
                .name("Test Work-Path2")
                .chapterId(chapterId)
                .stages(new HashSet<>())
                .build();

        workPathEntity = workPathRepository.save(workPathEntity);
        workPathEntity2 = workPathRepository.save(workPathEntity2);

        StageEntity stageEntity = StageEntity.builder()
                .workPathId(workPathEntity.getId())
                .position(0)
                .optionalContent(new HashSet<>())
                .requiredContents(new HashSet<>())
                .build();
        StageEntity stageEntity2 = StageEntity.builder()
                .workPathId(workPathEntity2.getId())
                .position(0)
                .optionalContent(new HashSet<>())
                .requiredContents(new HashSet<>())
                .build();
        stageEntity = stageRepository.save(stageEntity);
        stageEntity2 = stageRepository.save(stageEntity2);

        workPathEntity.getStages().add(stageEntity);
        workPathEntity2.getStages().add(stageEntity2);

        workPathEntity = workPathRepository.save(workPathEntity);
        workPathEntity2 = workPathRepository.save(workPathEntity2);

        return List.of(workPathEntity,workPathEntity2);
    }
    @Test
    void testQueryWorkPathsByChapter(GraphQlTester tester){
        List<WorkPathEntity> entities = fillDatabase();

        String query = """
                query($chapterId: UUID!) {
                findWorkPathsByChapter(id: $chapterId){
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
        List<WorkPath> result = tester.document(query)
                .variable("chapterId", entities.get(0).getChapterId())
                .execute()
                .path("findWorkPathsByChapter")
                .entityList(WorkPath.class).get();

        for (WorkPath workPath: result) {
            boolean foundEntity = false;
            // completeness checks
            assertNotNull(workPath);
            assertNotNull(workPath.getId());
            assertNotNull(workPath.getChapterId());
            assertNotNull(workPath.getName());
            assertNotNull(workPath.getStages());
            assertFalse(workPath.getStages().isEmpty());

            // entity comparison
            for (WorkPathEntity workPathEntity: entities) {
                if (!workPath.getId().equals(workPathEntity.getId())){
                    continue;
                }
                foundEntity = true;
                assertEquals(workPathEntity.getChapterId(), workPath.getChapterId());
                assertEquals(workPathEntity.getName(), workPath.getName());
                assertEquals(workPathEntity.getStages().size(), workPath.getStages().size());
            }

            assertTrue(foundEntity, "Entity was successfully retrieved");

        }
    }
}
