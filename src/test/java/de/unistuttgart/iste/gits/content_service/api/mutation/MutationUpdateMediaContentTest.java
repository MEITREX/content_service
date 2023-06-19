package de.unistuttgart.iste.gits.content_service.api.mutation;

import de.unistuttgart.iste.gits.common.testutil.GitsPostgresSqlContainer;
import de.unistuttgart.iste.gits.common.testutil.GraphQlApiTest;
import de.unistuttgart.iste.gits.content_service.TestData;
import de.unistuttgart.iste.gits.content_service.persistence.dao.ContentEntity;
import de.unistuttgart.iste.gits.content_service.persistence.dao.MediaContentEntity;
import de.unistuttgart.iste.gits.content_service.persistence.repository.ContentRepository;
import de.unistuttgart.iste.gits.generated.dto.ContentType;
import de.unistuttgart.iste.gits.generated.dto.MediaContent;
import de.unistuttgart.iste.gits.generated.dto.UpdateContentMetadataInput;
import de.unistuttgart.iste.gits.generated.dto.UpdateMediaContentInput;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.graphql.test.tester.GraphQlTester;
import org.testcontainers.junit.jupiter.Container;

import java.util.List;
import java.util.UUID;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

@GraphQlApiTest
class MutationUpdateMediaContentTest {

    @Container
    static final GitsPostgresSqlContainer postgres = GitsPostgresSqlContainer.getInstance();

    @Autowired
    private ContentRepository contentRepository;

    /**
     * Given a valid UpdateMediaContentInput
     * When the updateMediaContent mutation is called
     * Then the mediaContent is updated
     */
    @Test
    @Transactional
    void testUpdateMediaContent(GraphQlTester graphQlTester) {
        ContentEntity contentEntity = contentRepository.save(
                TestData.dummyMediaContentEntityBuilder().build());

        UpdateMediaContentInput input = UpdateMediaContentInput.builder()
                .setId(contentEntity.getId())
                .setMetadata(UpdateContentMetadataInput.builder()
                        .setChapterId(UUID.randomUUID())
                        .setName("newName")
                        .setRewardPoints(3)
                        .setTagNames(List.of("newTag1", "newTag2"))
                        .build())
                .build();

        String query = """
                mutation($input: UpdateMediaContentInput!) {
                    updateMediaContent(input: $input) {
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

        MediaContent updatedMediaContent = graphQlTester.document(query)
                .variable("input", input)
                .execute()
                .path("updateMediaContent").entity(MediaContent.class).get();

        // check that returned mediaContent is correct
        assertThat(updatedMediaContent.getId(), is(notNullValue()));
        assertThat(updatedMediaContent.getMetadata().getName(), is("newName"));
        assertThat(updatedMediaContent.getMetadata().getTagNames(), containsInAnyOrder("newTag1", "newTag2"));
        assertThat(updatedMediaContent.getMetadata().getType(), is(ContentType.MEDIA));
        assertThat(updatedMediaContent.getMetadata().getChapterId(), is(input.getMetadata().getChapterId()));
        assertThat(updatedMediaContent.getMetadata().getRewardPoints(), is(3));

        ContentEntity newContentEntity = contentRepository.findById(updatedMediaContent.getId()).orElseThrow();
        assertThat(newContentEntity, is(instanceOf(MediaContentEntity.class)));

        MediaContentEntity mediaContentEntity = (MediaContentEntity) newContentEntity;

        // check that mediaContent entity is correct
        assertThat(mediaContentEntity.getMetadata().getName(), is("newName"));
        assertThat(mediaContentEntity.getMetadata().getRewardPoints(), is(3));
        assertThat(mediaContentEntity.getTagNames(), containsInAnyOrder("newTag1", "newTag2"));
        assertThat(mediaContentEntity.getMetadata().getType(), is(ContentType.MEDIA));
        assertThat(mediaContentEntity.getMetadata().getChapterId(), is(input.getMetadata().getChapterId()));
    }
}
