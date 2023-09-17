package de.unistuttgart.iste.gits.content_service.api.query;

import de.unistuttgart.iste.gits.common.testutil.GraphQlApiTest;
import de.unistuttgart.iste.gits.content_service.TestData;
import de.unistuttgart.iste.gits.content_service.persistence.entity.ContentEntity;
import de.unistuttgart.iste.gits.content_service.persistence.repository.ContentRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.graphql.test.tester.GraphQlTester;

import java.util.List;
import java.util.UUID;

@GraphQlApiTest
class QueryByCourseIdTest {

    @Autowired
    private ContentRepository contentRepository;

    /**
     * Given valid courseIds
     * When the queryByCourseId query is called
     * Then the content is returned, correctly grouped and filtered by courseId
     */
    @Test
    void testQueryByCourseId(GraphQlTester graphQlTester) {
        List<UUID> courseIds = List.of(UUID.randomUUID(), UUID.randomUUID(), UUID.randomUUID(), UUID.randomUUID());

        courseIds.stream()
                .<ContentEntity>map(courseId -> (
                        TestData.dummyMediaContentEntityBuilder()
                                .metadata(TestData.dummyContentMetadataEmbeddableBuilder()
                                        .courseId(courseId)
                                        .build()))
                        .build())
                .forEach(contentRepository::save);

        String query = """
                query($courseIds: [UUID!]!) {
                    contentsByCourseIds(courseIds: $courseIds) {
                        id
                        metadata {
                            courseId
                        }
                    }
                }
                """;

        graphQlTester.document(query)
                .variable("courseIds", courseIds.subList(0, 3))
                .execute()
                .path("contentsByCourseIds[*][*].metadata.courseId")
                .entityList(UUID.class)
                .containsExactly(courseIds.get(0), courseIds.get(1), courseIds.get(2));
    }
}
