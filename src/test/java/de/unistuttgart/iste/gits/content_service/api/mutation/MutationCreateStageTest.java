package de.unistuttgart.iste.gits.content_service.api.mutation;

import de.unistuttgart.iste.gits.common.testutil.GraphQlApiTest;
import de.unistuttgart.iste.gits.content_service.persistence.dao.WorkPathEntity;
import de.unistuttgart.iste.gits.content_service.persistence.repository.WorkPathRepository;
import de.unistuttgart.iste.gits.generated.dto.Stage;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.graphql.test.tester.GraphQlTester;

import java.util.HashSet;
import java.util.UUID;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.jupiter.api.Assertions.*;

@GraphQlApiTest
class MutationCreateStageTest {

    @Autowired
    private WorkPathRepository workPathRepository;

    @Test
    void testStageCreation(GraphQlTester tester){
        WorkPathEntity workPathEntity = WorkPathEntity.builder()
                .id(UUID.randomUUID())
                .name("Test Work-Path")
                .chapterId(UUID.randomUUID())
                .stages(new HashSet<>())
                .build();
        workPathRepository.save(workPathEntity);

        String query = """
                mutation {
                createStage(workPathId: "%s") {
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
                """.formatted(UUID.randomUUID());
        tester.document(query)
                .execute()
                .path("createStage")
                .entity(Stage.class)
                .satisfies( stage -> {
            assertEquals(0, stage.getPosition());
            assertTrue(stage.getRequiredContents().isEmpty());
            assertTrue(stage.getOptionalContents().isEmpty());
            assertNotNull(stage.getId());
        });
    }
}
