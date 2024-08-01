package de.unistuttgart.iste.meitrex.content_service.api.query;

import de.unistuttgart.iste.meitrex.common.testutil.*;
import de.unistuttgart.iste.meitrex.common.user_handling.LoggedInUser;
import de.unistuttgart.iste.meitrex.content_service.TestData;
import de.unistuttgart.iste.meitrex.content_service.persistence.entity.AssessmentEntity;
import de.unistuttgart.iste.meitrex.content_service.persistence.entity.SkillEntity;
import de.unistuttgart.iste.meitrex.content_service.persistence.repository.ContentRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.graphql.test.tester.GraphQlTester;

import static de.unistuttgart.iste.meitrex.common.testutil.TestUsers.userWithMembershipInCourseWithId;

import java.util.List;
import java.util.UUID;

@GraphQlApiTest
public class QueryAchievableSkillsByCourseIdsTest {

    @Autowired
    private ContentRepository contentRepository;

    private final UUID courseId = UUID.randomUUID();

    @InjectCurrentUserHeader
    private final LoggedInUser loggedInUser = userWithMembershipInCourseWithId(courseId, LoggedInUser.UserRoleInCourse.STUDENT);

    @Test
    void testQueryAchievableSkillsByCourseIds(final GraphQlTester graphQlTester) {
        final UUID chapterId = UUID.randomUUID();
        final UUID courseId = UUID.randomUUID();
        final UUID chapterId2 = UUID.randomUUID();
        AssessmentEntity assessmentEntity1 = TestData.assessmentEntityWithItems(courseId, chapterId);

        assessmentEntity1 = contentRepository.save(assessmentEntity1);


        final String query = """
                query ($courseId: UUID!) {
                   _internal_noauth_achievableSkillsByCourseIds(courseIds: [$courseId]){
                       id
                       skillName
                   }
                }
                """;
        graphQlTester.document(query)
                .variable("courseId", courseId)
                .execute()
                .path("_internal_noauth_achievableSkillsByCourseIds[0][*]")
                .entityList(SkillEntity.class)
                .contains(assessmentEntity1.getItems().get(0).getAssociatedSkills().get(0));

    }
}
