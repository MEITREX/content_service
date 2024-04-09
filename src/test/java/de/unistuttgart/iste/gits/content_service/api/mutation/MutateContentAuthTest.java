package de.unistuttgart.iste.gits.content_service.api.mutation;

import de.unistuttgart.iste.meitrex.common.testutil.*;
import de.unistuttgart.iste.meitrex.common.user_handling.LoggedInUser;
import de.unistuttgart.iste.meitrex.common.user_handling.LoggedInUser.UserRoleInCourse;
import de.unistuttgart.iste.gits.content_service.TestData;
import de.unistuttgart.iste.gits.content_service.persistence.entity.ContentEntity;
import de.unistuttgart.iste.gits.content_service.persistence.repository.ContentRepository;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.graphql.test.tester.GraphQlTester;
import org.springframework.test.annotation.Commit;

import java.util.UUID;

import static de.unistuttgart.iste.meitrex.common.testutil.TestUsers.userWithMembershipInCourseWithId;

@GraphQlApiTest
@TablesToDelete({"stage_required_contents", "stage_optional_contents", "stage", "section", "content_tags", "user_progress_data_progress_log", "user_progress_data", "content_items","content"})
class MutateContentAuthTest {

    @Autowired
    private ContentRepository contentRepository;
    private final UUID courseId = UUID.randomUUID();

    @InjectCurrentUserHeader
    private final LoggedInUser loggedInUser = userWithMembershipInCourseWithId(courseId, UserRoleInCourse.TUTOR);

    @Test
    @Transactional
    @Commit
    void testMutateContentWithTutorRole(final GraphQlTester graphQlTester) {
        final ContentEntity content = contentRepository.save(TestData.dummyMediaContentEntityBuilder(courseId).build());
        final UUID contentId = content.getId();

        final String query = """
                mutation ($contentId: UUID!) {
                    mutateContent(contentId: $contentId) {
                        contentId
                    }
                }
                """;

        graphQlTester.document(query)
                .variable("contentId", contentId)
                .execute()
                .errors()
                .satisfy(AuthorizationAsserts::assertIsMissingUserRoleError);
    }
}
