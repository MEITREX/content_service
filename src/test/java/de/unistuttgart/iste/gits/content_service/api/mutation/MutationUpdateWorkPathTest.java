package de.unistuttgart.iste.gits.content_service.api.mutation;

import de.unistuttgart.iste.gits.common.testutil.GraphQlApiTest;
import de.unistuttgart.iste.gits.common.testutil.TablesToDelete;
import de.unistuttgart.iste.gits.content_service.persistence.dao.WorkPathEntity;
import de.unistuttgart.iste.gits.content_service.persistence.repository.WorkPathRepository;
import de.unistuttgart.iste.gits.generated.dto.UpdateWorkPathInput;
import de.unistuttgart.iste.gits.generated.dto.WorkPath;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.graphql.test.tester.GraphQlTester;

import java.util.HashSet;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@GraphQlApiTest
@TablesToDelete({"stage_required_contents", "stage_optional_content", "stage" ,"work_path" ,  "content_tags", "user_progress_data", "content", "tag"})
class MutationUpdateWorkPathTest {

    @Autowired
    private WorkPathRepository workPathRepository;

    @Test
    void testWorkPathUpdate(GraphQlTester tester){

        // fill database
        WorkPathEntity workPathEntity = WorkPathEntity.builder()
                .name("Test Work-Path")
                .chapterId(UUID.randomUUID())
                .stages(new HashSet<>())
                .build();
        workPathEntity = workPathRepository.save(workPathEntity);

        UpdateWorkPathInput input = UpdateWorkPathInput.builder()
                .setId(workPathEntity.getId())
                .setName("New Name")
                .build();

        String query = """
                mutation ($input: UpdateWorkPathInput!){
                    updateWorkPath(input: $input){
                    id
                    name
                    chapterId
                    stages {
                        id                 
                        }
                    }
                }
                """;

        WorkPathEntity finalWorkPathEntity = workPathEntity;

        tester.document(query)
                .variable("input", input)
                .execute()
                .path("updateWorkPath")
                .entity(WorkPath.class)
                .satisfies(workPath -> {
                          assertEquals(finalWorkPathEntity.getId(), workPath.getId());
                          assertEquals(finalWorkPathEntity.getChapterId(), workPath.getChapterId());
                          assertEquals(input.getName(), workPath.getName());
                          assertTrue(workPath.getStages().isEmpty());
                      }
                );
    }
}
