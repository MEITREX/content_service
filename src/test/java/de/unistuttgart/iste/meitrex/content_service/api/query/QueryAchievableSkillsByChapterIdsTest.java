package de.unistuttgart.iste.meitrex.content_service.api.query;

import de.unistuttgart.iste.meitrex.common.testutil.*;
import de.unistuttgart.iste.meitrex.common.user_handling.LoggedInUser;
import de.unistuttgart.iste.meitrex.content_service.TestData;
import de.unistuttgart.iste.meitrex.content_service.persistence.entity.AssessmentEntity;
import de.unistuttgart.iste.meitrex.content_service.persistence.entity.SkillEntity;
import de.unistuttgart.iste.meitrex.content_service.persistence.repository.ContentRepository;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.graphql.test.tester.GraphQlTester;
import org.springframework.test.annotation.Commit;

import static de.unistuttgart.iste.meitrex.common.testutil.TestUsers.userWithMembershipInCourseWithId;

import java.util.UUID;

@GraphQlApiTest
public class QueryAchievableSkillsByChapterIdsTest {
    @Autowired
    private ContentRepository contentRepository;

    private final UUID courseId = UUID.randomUUID();

    @InjectCurrentUserHeader
    private final LoggedInUser loggedInUser = userWithMembershipInCourseWithId(courseId, LoggedInUser.UserRoleInCourse.STUDENT);

    @Test
    @Commit
    @Transactional
    void testQueryAchievableSkillsByChapterIds(final GraphQlTester graphQlTester) {
        final UUID chapterId = UUID.randomUUID();
        final UUID courseId = UUID.randomUUID();

        AssessmentEntity assessmentEntity1 = TestData.assessmentEntityWithItems(courseId, chapterId);

        assessmentEntity1 = contentRepository.save(assessmentEntity1);

        final String query = """
                query ($chapterId: UUID!) {
                   _internal_noauth_achievableSkillsByChapterIds(chapterIds: [$chapterId]){
                        id
                        skillName
                        skillCategory
                        isCustomSkill
                   }
                }
                """;

        graphQlTester.document(query)
                .variable("chapterId", chapterId)
                .execute()
                .path("_internal_noauth_achievableSkillsByChapterIds[0][*]")
                .entityList(SkillEntity.class)
                .contains(assessmentEntity1.getItems().get(0).getAssociatedSkills().get(0));

    }
}
