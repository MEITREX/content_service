package de.unistuttgart.iste.meitrex.content_service.api.query;

import de.unistuttgart.iste.meitrex.common.testutil.GraphQlApiTest;
import de.unistuttgart.iste.meitrex.common.testutil.InjectCurrentUserHeader;
import de.unistuttgart.iste.meitrex.common.user_handling.LoggedInUser;
import de.unistuttgart.iste.meitrex.content_service.TestData;
import de.unistuttgart.iste.meitrex.content_service.persistence.entity.MediaContentEntity;
import de.unistuttgart.iste.meitrex.content_service.persistence.repository.ContentRepository;
import de.unistuttgart.iste.meitrex.content_service.persistence.repository.UserProgressDataRepository;
import de.unistuttgart.iste.meitrex.generated.dto.CompositeProgressInformation;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.graphql.test.tester.HttpGraphQlTester;

import java.util.List;
import java.util.UUID;

import static de.unistuttgart.iste.meitrex.common.testutil.TestUsers.userWithMembershipInCourseWithId;
import static org.junit.jupiter.api.Assertions.assertEquals;

@GraphQlApiTest
class QueryProgressByChapterIdTest {

    private static final String QUERY_USER_PROGRESS_BY_CHAPTER_IDS = """ 
            query($chapterId: UUID!, $userId: UUID!) {
                _internal_noauth_progressByChapterId(chapterId: $chapterId, userId: $userId){
                    progress
                    completedContents
                    totalContents
                }
            }
            """;

    @Autowired
    private ContentRepository contentRepository;

    @Autowired
    private UserProgressDataRepository userProgressDataRepository;

    private final UUID courseId = UUID.randomUUID();

    @InjectCurrentUserHeader
    private final LoggedInUser loggedInUser = userWithMembershipInCourseWithId(courseId, LoggedInUser.UserRoleInCourse.STUDENT);


    /**
     * This Testcase assumes Progress has already been made for all content within a chapter
     */
    @Test
    void testProgressByChapterId(HttpGraphQlTester graphQlTester) {
        UUID userId = loggedInUser.getId();
        UUID chapterId = UUID.randomUUID();
        MediaContentEntity mediaContentEntity = contentRepository.save(TestData.buildContentEntity(chapterId));
        MediaContentEntity mediaContentEntity1 = contentRepository.save(TestData.buildContentEntity(chapterId));

        userProgressDataRepository.save(TestData.buildDummyUserProgressData(true, userId, mediaContentEntity.getId()));
        userProgressDataRepository.save(TestData.buildDummyUserProgressData(false, userId, mediaContentEntity1.getId()));

        CompositeProgressInformation result = executeProgressByChapterIdQuery(graphQlTester, chapterId, userId);

        assertEquals(50.0, result.getProgress());
        assertEquals(1, result.getCompletedContents());
        assertEquals(2, result.getTotalContents());
    }

    /**
     * This Testcase assumes no Progress has already been made for all content within a chapter
     */
    @Test
    void testProgressByChapterIdWithNoProgress(HttpGraphQlTester graphQlTester) {
        UUID chapterId = UUID.randomUUID();
        UUID userId = loggedInUser.getId();
        contentRepository.save(TestData.buildContentEntity(chapterId));
        contentRepository.save(TestData.buildContentEntity(chapterId));

        CompositeProgressInformation result = executeProgressByChapterIdQuery(graphQlTester, chapterId, userId);

        assertEquals(0.0, result.getProgress());
        assertEquals(0, result.getCompletedContents());
        assertEquals(2, result.getTotalContents());
    }

    /**
     * This Testcase assumes no content exists for the chapter
     */
    @Test
    void testProgressByChapterIdWithNoContent(HttpGraphQlTester graphQlTester) {
        UUID chapterId = UUID.randomUUID();
        UUID userId = loggedInUser.getId();
        CompositeProgressInformation result = executeProgressByChapterIdQuery(graphQlTester, chapterId, userId);


        assertEquals(100.0, result.getProgress());
        assertEquals(0, result.getCompletedContents());
        assertEquals(0, result.getTotalContents());
    }

    private CompositeProgressInformation executeProgressByChapterIdQuery(HttpGraphQlTester graphQlTester,
                                                                         UUID chapterId, UUID userId) {


        return graphQlTester
                .document(QUERY_USER_PROGRESS_BY_CHAPTER_IDS)
                .variable("chapterId", chapterId)
                .variable("userId", userId)
                .execute()
                .path("_internal_noauth_progressByChapterId")
                .entity(CompositeProgressInformation.class).get();
    }
}
