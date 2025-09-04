package de.unistuttgart.iste.meitrex.content_service.api.mutation;

import de.unistuttgart.iste.meitrex.common.testutil.*;
import de.unistuttgart.iste.meitrex.common.user_handling.LoggedInUser;
import de.unistuttgart.iste.meitrex.common.user_handling.LoggedInUser.UserRoleInCourse;
import de.unistuttgart.iste.meitrex.content_service.TestData;
import de.unistuttgart.iste.meitrex.content_service.persistence.entity.AssessmentEntity;
import de.unistuttgart.iste.meitrex.content_service.persistence.entity.ContentEntity;
import de.unistuttgart.iste.meitrex.content_service.persistence.entity.SkillEntity;
import de.unistuttgart.iste.meitrex.content_service.persistence.repository.ContentRepository;
import de.unistuttgart.iste.meitrex.generated.dto.*;

import jakarta.transaction.Transactional;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.graphql.test.tester.GraphQlTester;
import org.springframework.test.annotation.Commit;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;

import static de.unistuttgart.iste.meitrex.common.testutil.TestUsers.userWithMembershipInCourseWithId;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

@GraphQlApiTest
class MutationUpdateAssessmentTest {

    @Autowired
    private ContentRepository contentRepository;

    private final UUID courseId = UUID.randomUUID();

    @InjectCurrentUserHeader
    private final LoggedInUser loggedInUser = userWithMembershipInCourseWithId(courseId, UserRoleInCourse.ADMINISTRATOR);

    /**
     * Given a valid UpdateAssessmentInput
     * When the updateAssessment mutation is called
     * Then the assessment is updated
     */
    @Test
    @Transactional
    @Commit
    void testUpdateAssessment(final GraphQlTester graphQlTester) {
        AssessmentEntity ass = TestData.dummyAssessmentEntityBuilderWithItems(courseId).build();
        final ContentEntity contentEntity = contentRepository.save(
                TestData.dummyAssessmentEntityBuilderWithItems(courseId).build());
        final UUID newChapterId = UUID.randomUUID();
        final String query = """
                mutation($assessmentId: UUID!, $chapterId: UUID!,$itemId:UUID!,$skillId:UUID!) {
                    mutateContent(contentId: $assessmentId){
                        updateAssessment(input: {
                            metadata: {
                                name: "newName",
                                suggestedDate: "2022-01-01T00:00:00.000Z",
                                chapterId: $chapterId,
                                rewardPoints: 3,
                                tagNames: ["newTag1", "newTag2"]
                            },
                            assessmentMetadata: {
                                skillPoints: 3,
                                skillTypes: [UNDERSTAND, REMEMBER]
                                initialLearningInterval: 7
                            },
                             items:[
                                    {
                                      id:$itemId
                                      associatedSkills:[{skillName:"abc",skillCategory:"abc-category",isCustomSkill:true}]
                                      associatedBloomLevels:[REMEMBER]
                            },{
                                      id:null
                                      associatedSkills:[{skillName:"name",skillCategory:"category",isCustomSkill:false, id:$skillId}]
                                      associatedBloomLevels:[REMEMBER]
                            }
                            ]
                        }) {
                            __typename
                            id
                            metadata {
                                name
                                suggestedDate
                                tagNames
                                type
                                chapterId
                                rewardPoints
                            }
                            items {
                                id
                                associatedSkills{
                                    skillName
                                    skillCategory
                                    isCustomSkill
                                }
                                associatedBloomLevels
                            }
                            assessmentMetadata {
                                skillPoints
                                skillTypes
                                initialLearningInterval
                            }
                        }
                    }
                }
                """;
        AssessmentEntity assessment = (AssessmentEntity) contentEntity;
        final FlashcardSetAssessment updatedAssessment = graphQlTester.document(query)
                .variable("assessmentId", contentEntity.getId())
                .variable("chapterId", newChapterId)
                .variable("itemId", assessment.getItems().get(0).getId())
                .variable("skillId", assessment.getItems().get(0).getAssociatedSkills().get(0).getId())
                .execute()
                .path("mutateContent.updateAssessment").entity(FlashcardSetAssessment.class).get();
        // check that returned assessment is correct
        assertThat(updatedAssessment.getId(), is(notNullValue()));
        assertThat(updatedAssessment.getMetadata().getName(), is("newName"));
        assertThat(updatedAssessment.getMetadata().getSuggestedDate(),
                is(OffsetDateTime.parse("2022-01-01T00:00:00.000Z")));
        assertThat(updatedAssessment.getMetadata().getTagNames(), containsInAnyOrder("newTag1", "newTag2"));
        assertThat(updatedAssessment.getMetadata().getType(), is(ContentType.FLASHCARDS));
        assertThat(updatedAssessment.getMetadata().getChapterId(), is(newChapterId));
        assertThat(updatedAssessment.getMetadata().getRewardPoints(), is(3));
        assertThat(updatedAssessment.getAssessmentMetadata().getSkillPoints(), is(3));
        assertThat(updatedAssessment.getAssessmentMetadata().getSkillTypes(), is(List.of(SkillType.UNDERSTAND, SkillType.REMEMBER)));
        assertThat(updatedAssessment.getAssessmentMetadata().getInitialLearningInterval(), is(7));
        assertThat(updatedAssessment.getItems().size(), is(2));
        assertThat(updatedAssessment.getItems().get(0).getId(), is(assessment.getItems().get(0).getId()));
        assertThat(updatedAssessment.getItems().get(0).getAssociatedBloomLevels(), is(List.of(BloomLevel.REMEMBER)));
        assertThat(updatedAssessment.getItems().get(0).getAssociatedSkills().get(0).getSkillName(), is("abc"));
        assertThat(updatedAssessment.getItems().get(0).getAssociatedSkills().get(0).getSkillCategory(), is("abc-category"));
        assertThat(updatedAssessment.getItems().get(0).getAssociatedSkills().get(0).getIsCustomSkill(), is(true));

        final ContentEntity newContentEntity = contentRepository.findById(updatedAssessment.getId()).orElseThrow();
        assertThat(newContentEntity, is(instanceOf(AssessmentEntity.class)));

        final AssessmentEntity assessmentEntity = (AssessmentEntity) newContentEntity;

        // check that assessment entity is correct
        assertThat(assessmentEntity.getMetadata().getName(), is("newName"));
        assertThat(assessmentEntity.getMetadata().getSuggestedDate(),
                is(OffsetDateTime.parse("2022-01-01T00:00:00.000Z")));
        assertThat(assessmentEntity.getMetadata().getRewardPoints(), is(3));
        assertThat(assessmentEntity.getMetadata().getTags(), containsInAnyOrder("newTag1", "newTag2"));
        assertThat(assessmentEntity.getMetadata().getType(), is(ContentType.FLASHCARDS));
        assertThat(assessmentEntity.getMetadata().getChapterId(), is(newChapterId));
        assertThat(assessmentEntity.getAssessmentMetadata().getSkillPoints(), is(3));
        assertThat(assessmentEntity.getAssessmentMetadata().getSkillTypes(), is(List.of(SkillType.UNDERSTAND, SkillType.REMEMBER)));
        assertThat(assessmentEntity.getAssessmentMetadata().getInitialLearningInterval(), is(7));
        assertThat(assessmentEntity.getItems().size(), is(2));
        assertThat(assessmentEntity.getItems().get(0).getId(), is(assessment.getItems().get(0).getId()));
        assertThat(assessmentEntity.getItems().get(0).getAssociatedBloomLevels(), is(List.of(BloomLevel.REMEMBER)));
        assertThat(assessmentEntity.getItems().get(0).getAssociatedSkills().get(0).getSkillName(), is("abc"));
        assertThat(assessmentEntity.getItems().get(0).getAssociatedSkills().get(0).getSkillCategory(), is("abc-category"));
        assertThat(assessmentEntity.getItems().get(0).getAssociatedSkills().get(0).getIsCustomSkill(), is(true));
    }
}
