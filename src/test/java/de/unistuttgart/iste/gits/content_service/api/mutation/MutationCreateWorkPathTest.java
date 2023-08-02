package de.unistuttgart.iste.gits.content_service.api.mutation;

import de.unistuttgart.iste.gits.common.testutil.GraphQlApiTest;
import de.unistuttgart.iste.gits.common.testutil.TablesToDelete;
import de.unistuttgart.iste.gits.generated.dto.CreateWorkPathInput;
import de.unistuttgart.iste.gits.generated.dto.WorkPath;
import org.junit.jupiter.api.Test;
import org.springframework.graphql.test.tester.GraphQlTester;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@GraphQlApiTest
@TablesToDelete({"stage_required_contents", "stage_optional_content", "stage" ,"work_path" ,  "content_tags", "user_progress_data", "content", "tag"})
class MutationCreateWorkPathTest {

    @Test
    void testWorkPathCreation(GraphQlTester tester){
        CreateWorkPathInput input = CreateWorkPathInput.builder()
                .setChapterId(UUID.randomUUID())
                .setName("Test Work-Path")
                .build();
        String query = """
                mutation ($input: CreateWorkPathInput!){
                    createWorkPath(input: $input){
                    id
                    name
                    chapterId
                    stages {
                        id
                        position
                        optionalContents {
                            id                        
                                        }
                        requiredContents {
                            id       
                                        }                    
                                    }
                    }
                }
                """;
        tester.document(query)
                .variable("input", input)
                .execute()
                .path("createWorkPath")
                .entity(WorkPath.class).satisfies( workPath -> {
                    assertNotNull(workPath.getId());
                    assertEquals(input.getName(), workPath.getName());
                    assertEquals(input.getChapterId() ,workPath.getChapterId());
                    assertTrue(workPath.getStages().isEmpty());
                });
    }
}
