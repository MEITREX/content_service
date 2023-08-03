package de.unistuttgart.iste.gits.content_service.api.mutation;

import de.unistuttgart.iste.gits.common.testutil.GraphQlApiTest;
import de.unistuttgart.iste.gits.common.testutil.TablesToDelete;
import de.unistuttgart.iste.gits.content_service.persistence.dao.SectionEntity;
import de.unistuttgart.iste.gits.content_service.persistence.repository.SectionRepository;
import de.unistuttgart.iste.gits.generated.dto.Stage;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.graphql.test.tester.GraphQlTester;

import java.util.HashSet;
import java.util.UUID;
import static org.junit.jupiter.api.Assertions.*;

@GraphQlApiTest
@TablesToDelete({"stage_required_contents", "stage_optional_content", "stage" ,"section" , "content_tags", "user_progress_data", "content", "tag"})
class MutationCreateStageTest {

    @Autowired
    private SectionRepository sectionRepository;

    @Test
    void testStageCreation(GraphQlTester tester){
        SectionEntity sectionEntity = SectionEntity.builder()
                .name("Test Section")
                .chapterId(UUID.randomUUID())
                .stages(new HashSet<>())
                .build();
        sectionEntity = sectionRepository.save(sectionEntity);

        String query = """
                mutation {
                createStage(sectionId: "%s") {
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
                """.formatted(sectionEntity.getId());
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
