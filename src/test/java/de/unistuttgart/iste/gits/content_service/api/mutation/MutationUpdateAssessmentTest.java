package de.unistuttgart.iste.gits.content_service.api.mutation;

import de.unistuttgart.iste.gits.common.testutil.GitsPostgresSqlContainer;
import de.unistuttgart.iste.gits.common.testutil.GraphQlApiTest;
import de.unistuttgart.iste.gits.content_service.TestData;
import de.unistuttgart.iste.gits.content_service.persistence.dao.AssessmentEntity;
import de.unistuttgart.iste.gits.content_service.persistence.dao.ContentEntity;
import de.unistuttgart.iste.gits.content_service.persistence.repository.ContentRepository;
import de.unistuttgart.iste.gits.generated.dto.*;
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
class MutationUpdateAssessmentTest {

    @Container
    static final GitsPostgresSqlContainer postgres = GitsPostgresSqlContainer.getInstance();

    @Autowired
    private ContentRepository contentRepository;

    /**
     * Given a valid UpdateAssessmentInput
     * When the updateAssessment mutation is called
     * Then the assessment is updated
     */
    @Test
    @Transactional
    void testUpdateAssessment(GraphQlTester graphQlTester) {
        ContentEntity contentEntity = contentRepository.save(
                TestData.dummyAssessmentEntityBuilder().build());

        UpdateAssessmentInput input = UpdateAssessmentInput.builder()
                .setId(contentEntity.getId())
                .setMetadata(UpdateContentMetadataInput.builder()
                        .setChapterId(UUID.randomUUID())
                        .setName("newName")
                        .setRewardPoints(3)
                        .setTagNames(List.of("newTag1", "newTag2"))
                        .build())
                .setAssessmentMetadata(AssessmentMetadataInput.builder()
                        .setSkillPoints(3)
                        .setSkillType(SkillType.UNDERSTAND)
                        .build())
                .build();

        String query = """
                mutation($input: UpdateAssessmentInput!) {
                    updateAssessment(input: $input) {
                        id
                        metadata {
                            name
                            tagNames
                            type
                            chapterId
                            rewardPoints
                        }
                        assessmentMetadata {
                            skillPoints
                            skillType
                        }
                    }
                }
                """;

        FlashcardSetAssessment updatedAssessment = graphQlTester.document(query)
                .variable("input", input)
                .execute()
                .path("updateAssessment").entity(FlashcardSetAssessment.class).get();

        // check that returned assessment is correct
        assertThat(updatedAssessment.getId(), is(notNullValue()));
        assertThat(updatedAssessment.getMetadata().getName(), is("newName"));
        assertThat(updatedAssessment.getMetadata().getTagNames(), containsInAnyOrder("newTag1", "newTag2"));
        assertThat(updatedAssessment.getMetadata().getType(), is(ContentType.FLASHCARDS));
        assertThat(updatedAssessment.getMetadata().getChapterId(), is(input.getMetadata().getChapterId()));
        assertThat(updatedAssessment.getMetadata().getRewardPoints(), is(3));
        assertThat(updatedAssessment.getAssessmentMetadata().getSkillPoints(), is(3));
        assertThat(updatedAssessment.getAssessmentMetadata().getSkillType(), is(SkillType.UNDERSTAND));

        ContentEntity newContentEntity = contentRepository.findById(updatedAssessment.getId()).orElseThrow();
        assertThat(newContentEntity, is(instanceOf(AssessmentEntity.class)));

        AssessmentEntity assessmentEntity = (AssessmentEntity) newContentEntity;

        // check that assessment entity is correct
        assertThat(assessmentEntity.getMetadata().getName(), is("newName"));
        assertThat(assessmentEntity.getMetadata().getRewardPoints(), is(3));
        assertThat(assessmentEntity.getTagNames(), containsInAnyOrder("newTag1", "newTag2"));
        assertThat(assessmentEntity.getMetadata().getType(), is(ContentType.FLASHCARDS));
        assertThat(assessmentEntity.getMetadata().getChapterId(), is(input.getMetadata().getChapterId()));
        assertThat(assessmentEntity.getAssessmentMetadata().getSkillPoints(), is(3));
        assertThat(assessmentEntity.getAssessmentMetadata().getSkillType(), is(SkillType.UNDERSTAND));
    }
}
