package de.unistuttgart.iste.gits.content_service.api.mutation;

import de.unistuttgart.iste.gits.common.testutil.GraphQlTesterParameterResolver;
import de.unistuttgart.iste.gits.content_service.persistence.dao.AssessmentEntity;
import de.unistuttgart.iste.gits.content_service.persistence.dao.ContentEntity;
import de.unistuttgart.iste.gits.content_service.persistence.repository.ContentRepository;
import de.unistuttgart.iste.gits.generated.dto.*;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.graphql.test.tester.GraphQlTester;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;
import java.util.UUID;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

@ExtendWith(GraphQlTesterParameterResolver.class)
@SpringBootTest
@ActiveProfiles("test")
class MutationCreateAssessmentTest {

    @Autowired
    private ContentRepository contentRepository;

    /**
     * Given a valid CreateAssessmentInput
     * When the createAssessment mutation is called
     * Then a new assessment is created
     */
    @Test
    @Transactional
    void testCreateAssessment(GraphQlTester graphQlTester) {
        CreateAssessmentInput input = CreateAssessmentInput.builder()
                .setMetadata(CreateContentMetadataInput.builder()
                        .setChapterId(UUID.randomUUID())
                        .setName("name")
                        .setRewardPoints(1)
                        .setTagNames(List.of("tag1", "tag2"))
                        .setType(ContentType.FLASHCARDS)
                        .build())
                .setAssessmentMetadata(AssessmentMetadataInput.builder()
                        .setSkillPoints(1)
                        .setSkillType(SkillType.REMEMBER)
                        .build())
                .build();

        String query = """
                mutation($input: CreateAssessmentInput!) {
                    createAssessment(input: $input) {
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

        FlashcardSetAssessment createdAssessment = graphQlTester.document(query)
                .variable("input", input)
                .execute()
                .path("createAssessment").entity(FlashcardSetAssessment.class).get();

        // check that returned assessment is correct
        assertThat(createdAssessment.getId(), is(notNullValue()));
        assertThat(createdAssessment.getMetadata().getName(), is("name"));
        assertThat(createdAssessment.getMetadata().getTagNames(), containsInAnyOrder("tag1", "tag2"));
        assertThat(createdAssessment.getMetadata().getType(), is(ContentType.FLASHCARDS));
        assertThat(createdAssessment.getMetadata().getChapterId(), is(input.getMetadata().getChapterId()));
        assertThat(createdAssessment.getMetadata().getRewardPoints(), is(1));
        assertThat(createdAssessment.getAssessmentMetadata().getSkillPoints(), is(1));
        assertThat(createdAssessment.getAssessmentMetadata().getSkillType(), is(SkillType.REMEMBER));

        ContentEntity contentEntity = contentRepository.findById(createdAssessment.getId()).orElseThrow();
        assertThat(contentEntity, is(instanceOf(AssessmentEntity.class)));

        AssessmentEntity assessmentEntity = (AssessmentEntity) contentEntity;

        // check that assessment entity is correct
        assertThat(assessmentEntity.getMetadata().getName(), is("name"));
        assertThat(assessmentEntity.getTagNames(), containsInAnyOrder("tag1", "tag2"));
        assertThat(assessmentEntity.getMetadata().getType(), is(ContentType.FLASHCARDS));
        assertThat(assessmentEntity.getMetadata().getChapterId(), is(input.getMetadata().getChapterId()));
        assertThat(assessmentEntity.getMetadata().getRewardPoints(), is(1));
        assertThat(assessmentEntity.getAssessmentMetadata().getSkillPoints(), is(1));
        assertThat(assessmentEntity.getAssessmentMetadata().getSkillType(), is(SkillType.REMEMBER));
    }

    /**
     * Given a CreateAssessmentInput with content type MEDIA
     * When the createAssessment mutation is called
     * Then a ValidationException is thrown
     */
    @Test
    void testCreateAssessmentWithMediaContentType(GraphQlTester graphQlTester) {
        String query = """
                mutation {
                    createAssessment(input: {
                        metadata: {
                            type: MEDIA
                            name: "name"
                            chapterId: "00000000-0000-0000-0000-000000000000"
                            rewardPoints: 1
                            tagNames: ["tag1", "tag2"]
                        }
                        assessmentMetadata: {
                            skillPoints: 1
                            skillType: REMEMBER
                        }
                    }) { id }
                }
                """;

        graphQlTester.document(query)
                .execute()
                .errors()
                .satisfy(errors -> {
                    assertThat(errors, hasSize(1));
                    assertThat(errors.get(0).getMessage(), containsString("MEDIA is not a valid content type for an assessment"));
                    assertThat(errors.get(0).getExtensions().get("classification"), is("ValidationError"));
                });
    }
}
