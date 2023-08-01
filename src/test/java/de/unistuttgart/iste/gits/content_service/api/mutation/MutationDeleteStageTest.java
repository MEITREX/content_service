package de.unistuttgart.iste.gits.content_service.api.mutation;

import de.unistuttgart.iste.gits.common.testutil.GraphQlApiTest;
import de.unistuttgart.iste.gits.content_service.persistence.dao.StageEntity;
import de.unistuttgart.iste.gits.content_service.persistence.dao.WorkPathEntity;
import de.unistuttgart.iste.gits.content_service.persistence.repository.StageRepository;
import de.unistuttgart.iste.gits.content_service.persistence.repository.WorkPathRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.graphql.test.tester.GraphQlTester;

import java.util.HashSet;
import java.util.UUID;

@GraphQlApiTest
class MutationDeleteStageTest {

    @Autowired
    private WorkPathRepository workPathRepository;

    @Autowired
    private StageRepository stageRepository;

    @Test
    void testStageDeletion(GraphQlTester tester){
        // fill database
        WorkPathEntity workPathEntity = WorkPathEntity.builder()
                .name("Test Work-Path")
                .chapterId(UUID.randomUUID())
                .stages(new HashSet<>())
                .build();
        workPathEntity = workPathRepository.save(workPathEntity);

        StageEntity stageEntity = StageEntity.builder()
                .workPathId(workPathEntity.getId())
                .position(0)
                .optionalContent(new HashSet<>())
                .requiredContents(new HashSet<>())
                .build();
        stageEntity = stageRepository.save(stageEntity);

        String query = """
                mutation ($id: UUID!){
                deleteStage(id: $id)
                }
                """;
        tester.document(query)
                .variable("id", stageEntity.getId())
                .execute()
                .path("deleteStage")
                .entity(UUID.class)
                .isEqualTo(stageEntity.getId());
    }
}
