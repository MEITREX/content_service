package de.unistuttgart.iste.gits.content_service.api.mutation;

import de.unistuttgart.iste.gits.common.testutil.GraphQlApiTest;
import de.unistuttgart.iste.gits.content_service.persistence.dao.WorkPathEntity;
import de.unistuttgart.iste.gits.content_service.persistence.repository.WorkPathRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.graphql.test.tester.GraphQlTester;

import java.util.HashSet;
import java.util.UUID;

@GraphQlApiTest
class MutationDeleteWorkPathTest {

    @Autowired
    private WorkPathRepository workPathRepository;

    @Test

    void testWorkPathDeletion(GraphQlTester tester){
        WorkPathEntity workPathEntity = WorkPathEntity.builder()
                .name("Test Work-Path")
                .chapterId(UUID.randomUUID())
                .stages(new HashSet<>())
                .build();
        workPathEntity = workPathRepository.save(workPathEntity);

        String query = """
                mutation ($id: UUID!){
                deleteWorkPath(id: $id)
                }
                """;
        tester.document(query)
                .variable("id", workPathEntity.getId())
                .execute()
                .path("deleteWorkPath")
                .entity(UUID.class)
                .isEqualTo(workPathEntity.getId());
    }
}
