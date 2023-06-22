package de.unistuttgart.iste.gits.content_service.api.mutation;

import de.unistuttgart.iste.gits.common.dapr.CrudOperation;
import de.unistuttgart.iste.gits.common.testutil.GitsPostgresSqlContainer;
import de.unistuttgart.iste.gits.common.testutil.GraphQlApiTest;
import de.unistuttgart.iste.gits.content_service.dapr.TopicPublisher;
import de.unistuttgart.iste.gits.content_service.persistence.dao.ContentEntity;
import de.unistuttgart.iste.gits.content_service.persistence.dao.MediaContentEntity;
import de.unistuttgart.iste.gits.content_service.persistence.repository.ContentRepository;
import de.unistuttgart.iste.gits.content_service.test_config.MockTopicPublisherConfiguration;
import de.unistuttgart.iste.gits.generated.dto.ContentType;
import de.unistuttgart.iste.gits.generated.dto.CreateContentMetadataInput;
import de.unistuttgart.iste.gits.generated.dto.CreateMediaContentInput;
import de.unistuttgart.iste.gits.generated.dto.MediaContent;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.graphql.test.tester.GraphQlTester;
import org.springframework.test.context.ContextConfiguration;
import org.testcontainers.junit.jupiter.Container;

import java.util.List;
import java.util.UUID;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

@ContextConfiguration(classes = MockTopicPublisherConfiguration.class)
@GraphQlApiTest
class MutationCreateMediaContentTest {

    @Container
    static final GitsPostgresSqlContainer postgres = GitsPostgresSqlContainer.getInstance();

    @Autowired
    private ContentRepository contentRepository;

    @Autowired
    private TopicPublisher topicPublisher;

    /**
     * Given a valid CreateAssessmentInput
     * When the createAssessment mutation is called
     * Then a new assessment is created
     */
    @Test
    @Transactional
    void testCreateMediaContent(GraphQlTester graphQlTester) {
        CreateMediaContentInput input = CreateMediaContentInput.builder()
                .setMetadata(CreateContentMetadataInput.builder()
                        .setChapterId(UUID.randomUUID())
                        .setName("name")
                        .setRewardPoints(1)
                        .setTagNames(List.of("tag1", "tag2"))
                        .setType(ContentType.MEDIA)
                        .build())
                .build();

        String query = """
                mutation($input: CreateMediaContentInput!) {
                    createMediaContent(input: $input) {
                        id
                        metadata {
                            name
                            tagNames
                            type
                            chapterId
                            rewardPoints
                        }
                    }
                }
                """;

        MediaContent createdMediaContent = graphQlTester.document(query)
                .variable("input", input)
                .execute()
                .path("createMediaContent").entity(MediaContent.class).get();

        // check that returned mediaContent is correct
        assertThat(createdMediaContent.getId(), is(notNullValue()));
        assertThat(createdMediaContent.getMetadata().getName(), is("name"));
        assertThat(createdMediaContent.getMetadata().getTagNames(), containsInAnyOrder("tag1", "tag2"));
        assertThat(createdMediaContent.getMetadata().getType(), is(ContentType.MEDIA));
        assertThat(createdMediaContent.getMetadata().getChapterId(), is(input.getMetadata().getChapterId()));
        assertThat(createdMediaContent.getMetadata().getRewardPoints(), is(1));

        ContentEntity contentEntity = contentRepository.findById(createdMediaContent.getId()).orElseThrow();
        assertThat(contentEntity, is(instanceOf(MediaContentEntity.class)));

        MediaContentEntity mediaContentEntity = (MediaContentEntity) contentEntity;

        // check that mediaContent entity is correct
        assertThat(mediaContentEntity.getMetadata().getName(), is("name"));
        assertThat(mediaContentEntity.getTagNames(), containsInAnyOrder("tag1", "tag2"));
        assertThat(mediaContentEntity.getMetadata().getType(), is(ContentType.MEDIA));
        assertThat(mediaContentEntity.getMetadata().getChapterId(), is(input.getMetadata().getChapterId()));
        assertThat(mediaContentEntity.getMetadata().getRewardPoints(), is(1));

        Mockito.verify(topicPublisher, Mockito.atLeastOnce()).notifyChange(Mockito.any(ContentEntity.class), Mockito.eq(CrudOperation.CREATE));
    }

    /**
     * Given a CreateMediaContentInput with content type FLASHCARDS
     * When the createMediaContent mutation is called
     * Then a ValidationException is thrown
     */
    @Test
    void testCreateMediaContentWithFlashcardsType(GraphQlTester graphQlTester) {
        String query = """
                mutation {
                    createMediaContent(input: {
                        metadata: {
                            type: FLASHCARDS,
                            name: "name"
                            chapterId: "00000000-0000-0000-0000-000000000000"
                            rewardPoints: 1
                            tagNames: ["tag1", "tag2"]
                        }
                    }) { id }
                }
                """;

        graphQlTester.document(query)
                .execute()
                .errors()
                .satisfy(errors -> {
                    assertThat(errors, hasSize(1));
                    assertThat(errors.get(0).getMessage(), containsString("Media content must have type MEDIA"));
                    assertThat(errors.get(0).getExtensions().get("classification"), is("ValidationError"));
                });
    }
}
