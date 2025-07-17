package de.unistuttgart.iste.meitrex.content_service.client;

import de.unistuttgart.iste.meitrex.common.testutil.GraphQlApiTest;
import de.unistuttgart.iste.meitrex.content_service.TestData;
import de.unistuttgart.iste.meitrex.content_service.exception.ContentServiceConnectionException;
import de.unistuttgart.iste.meitrex.content_service.persistence.entity.AssessmentEntity;
import de.unistuttgart.iste.meitrex.content_service.persistence.entity.ContentEntity;
import de.unistuttgart.iste.meitrex.content_service.persistence.entity.MediaContentEntity;
import de.unistuttgart.iste.meitrex.content_service.persistence.repository.ContentRepository;
import de.unistuttgart.iste.meitrex.common.testutil.TablesToDelete;
import de.unistuttgart.iste.meitrex.content_service.persistence.repository.UserProgressDataRepository;
import de.unistuttgart.iste.meitrex.generated.dto.*;
import io.dapr.actors.runtime.ActorStateManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.graphql.client.ClientGraphQlResponse;
import org.springframework.graphql.client.ClientResponseField;
import org.springframework.graphql.client.GraphQlClient;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.springframework.test.web.servlet.client.MockMvcWebTestClient;
import org.springframework.web.context.WebApplicationContext;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.UUID;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.hamcrest.Matchers.hasSize;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.Assert.assertThrows;
import static org.mockito.Mockito.when;

/**
 * This class is used to test the ContentServiceClient.
 */
@GraphQlApiTest
class ContentServiceClientTest {

    private GraphQlClient graphQlClient;

    @Autowired
    private WebApplicationContext applicationContext;

    @Autowired
    private ContentRepository contentRepository;

    @Autowired
    private UserProgressDataRepository userProgressDataRepository;

    @BeforeEach
    void setUp() {
        final WebTestClient webTestClient = MockMvcWebTestClient.bindToApplicationContext(applicationContext)
                .configureClient().baseUrl("/graphql").build();

        graphQlClient = GraphQlClient.builder(new WebTestClientTransport(webTestClient)).build();
    }

    @Test
    void testQueryContentsOfChapter() throws Exception {
        final ContentServiceClient contentServiceClient = new ContentServiceClient(graphQlClient);
        final UUID courseId = UUID.randomUUID();
        final UUID chapterId = UUID.randomUUID();
        final UUID userId = UUID.randomUUID();

        contentRepository.save(createMediaContentForChapter(courseId, chapterId));
        contentRepository.save(createAssessmentForChapter(courseId, chapterId, ContentType.FLASHCARDS));
        contentRepository.save(createAssessmentForChapter(courseId, chapterId, ContentType.QUIZ));
        contentRepository.save(createAssessmentForChapter(courseId, chapterId, ContentType.ASSIGNMENT));

        final List<Content> actualContents = contentServiceClient.queryContentsOfChapter(userId, chapterId);

        assertThat(actualContents, hasSize(4));

        // we just check the types here exemplary, other fields are tested in the API tests
        final var types = actualContents.stream().map(Content::getMetadata).map(ContentMetadata::getType).toList();

        assertThat(types, containsInAnyOrder(ContentType.MEDIA, ContentType.FLASHCARDS, ContentType.QUIZ, ContentType.ASSIGNMENT));
    }

    @Test
    void testQueryContentsOfCourse() throws Exception {
        final ContentServiceClient contentServiceClient = new ContentServiceClient(graphQlClient);
        final UUID courseId = UUID.randomUUID();
        final UUID chapterId = UUID.randomUUID();
        final UUID userId = UUID.randomUUID();

        contentRepository.save(createMediaContentForChapter(courseId, chapterId));
        contentRepository.save(createAssessmentForChapter(courseId, chapterId, ContentType.FLASHCARDS));
        contentRepository.save(createAssessmentForChapter(courseId, chapterId, ContentType.QUIZ));
        contentRepository.save(createAssessmentForChapter(courseId, chapterId, ContentType.ASSIGNMENT));

        final List<Content> actualContents = contentServiceClient.queryContentsOfCourse(userId, courseId);

        assertThat(actualContents, hasSize(4));

        // we just check the types here exemplary, other fields are tested in the API tests
        final var types = actualContents.stream().map(Content::getMetadata).map(ContentMetadata::getType).toList();

        assertThat(types, containsInAnyOrder(ContentType.MEDIA, ContentType.FLASHCARDS, ContentType.QUIZ, ContentType.ASSIGNMENT));
    }

    @Test
    void testQueryContentsOfCourseThrowsContentServiceConnectionException() {
        UUID courseId = UUID.randomUUID();
        final UUID userId = UUID.randomUUID();
        GraphQlClient mockClient = Mockito.mock(GraphQlClient.class);
        final ContentServiceClient contentServiceClient = new ContentServiceClient(mockClient);
        ContentServiceConnectionException inner = new ContentServiceConnectionException("Connection error");
        RuntimeException wrapped = new RuntimeException(inner);

        when(mockClient.document(Mockito.anyString())).thenThrow(wrapped);

        assertThrows(ContentServiceConnectionException.class, () -> {
            contentServiceClient.queryContentsOfCourse(userId, courseId);
        });
    }

    @Test
    void testQueryContentIdsOfCourse() throws Exception {
        final ContentServiceClient contentServiceClient = new ContentServiceClient(graphQlClient);
        final UUID courseId = UUID.randomUUID();
        final UUID chapterId = UUID.randomUUID();

        contentRepository.save(createMediaContentForChapter(courseId, chapterId));
        contentRepository.save(createAssessmentForChapter(courseId, chapterId, ContentType.FLASHCARDS));
        contentRepository.save(createAssessmentForChapter(courseId, chapterId, ContentType.QUIZ));
        contentRepository.save(createAssessmentForChapter(courseId, chapterId, ContentType.ASSIGNMENT));

        final List<UUID> actualContents = contentServiceClient.queryContentIdsOfCourse(courseId);

        assertThat(actualContents, hasSize(4));
    }

    @Test
    void testQueryContentIdsOfCourseThrowsContentServiceConnectionException() {
        UUID courseId = UUID.randomUUID();
        GraphQlClient mockClient = Mockito.mock(GraphQlClient.class);
        final ContentServiceClient contentServiceClient = new ContentServiceClient(mockClient);
        ContentServiceConnectionException inner = new ContentServiceConnectionException("Connection error");
        RuntimeException wrapped = new RuntimeException(inner);

        when(mockClient.document(Mockito.anyString())).thenThrow(wrapped);

        assertThrows(ContentServiceConnectionException.class, () -> {
            contentServiceClient.queryContentIdsOfCourse(courseId);
        });
    }

    @Test
    void testQueryContentIdsOfCourse_throwsExceptionWhenResponseInvalid() {
        UUID courseId = UUID.randomUUID();
        GraphQlClient mockClient = Mockito.mock(GraphQlClient.class);
        GraphQlClient.RequestSpec requestSpecMock = Mockito.mock(GraphQlClient.RequestSpec.class);
        ClientGraphQlResponse clientResponseMock = Mockito.mock(ClientGraphQlResponse.class);
        final ContentServiceClient contentServiceClient = new ContentServiceClient(mockClient);
        ClientResponseField fieldMock = Mockito.mock(ClientResponseField.class);

        // Mock graphQlClient and response
        when(mockClient.document(Mockito.anyString()))
                .thenReturn(requestSpecMock);
        when(requestSpecMock.variable(Mockito.anyString(), Mockito.any()))
                .thenReturn(requestSpecMock);
        when(requestSpecMock.execute())
                .thenReturn(Mono.just(clientResponseMock));

        when(clientResponseMock.field(Mockito.anyString() + "[0]"))
                .thenReturn(fieldMock);

        when(fieldMock.getValue())
                .thenReturn(null);

        assertThrows(ContentServiceConnectionException.class,
                () -> contentServiceClient.queryContentIdsOfCourse(courseId));
    }

    @Test
    void testQueryContentsByIds() throws Exception {
        final ContentServiceClient contentServiceClient = new ContentServiceClient(graphQlClient);
        final UUID courseId = UUID.randomUUID();
        final UUID chapterId = UUID.randomUUID();
        final UUID userId = UUID.randomUUID();

        ContentEntity content1 = contentRepository.save(createMediaContentForChapter(courseId, chapterId));
        ContentEntity content2 = contentRepository.save(createAssessmentForChapter(courseId, chapterId, ContentType.FLASHCARDS));
        ContentEntity content3 = contentRepository.save(createAssessmentForChapter(courseId, chapterId, ContentType.QUIZ));

        final List<Content> actualContents = contentServiceClient.queryContentsByIds(userId, List.of(content1.getId(), content2.getId(), content3.getId()));
        System.out.println(actualContents.toString());
        assertThat(actualContents, hasSize(3));

        // we just check the types here exemplary, other fields are tested in the API tests
        final var types = actualContents.stream().map(Content::getMetadata).map(ContentMetadata::getType).toList();

        assertThat(types, containsInAnyOrder(ContentType.MEDIA, ContentType.FLASHCARDS, ContentType.QUIZ));
    }

    @Test
    void testQueryProgressByContentId() throws Exception {
        final ContentServiceClient contentServiceClient = new ContentServiceClient(graphQlClient);
        UUID userId = UUID.randomUUID();
        UUID chapterId = UUID.randomUUID();
        MediaContentEntity mediaContentEntity = contentRepository.save(TestData.buildContentEntity(chapterId));
        MediaContentEntity mediaContentEntity1 = contentRepository.save(TestData.buildContentEntity(chapterId));

        userProgressDataRepository.save(TestData.buildDummyUserProgressData(true, userId, mediaContentEntity.getId()));
        userProgressDataRepository.save(TestData.buildDummyUserProgressData(false, userId, mediaContentEntity1.getId()));
        final CompositeProgressInformation actualProgress = contentServiceClient.queryProgressByChapterId(userId, chapterId);
        System.out.println(actualProgress.toString());

        // we just check the types here exemplary, other fields are tested in the API tests
        assertEquals(50.0, actualProgress.getProgress());
        assertEquals(1, actualProgress.getCompletedContents());
        assertEquals(2, actualProgress.getTotalContents());
    }

    private ContentEntity createMediaContentForChapter(final UUID courseId, final UUID chapterId) {
        return TestData.dummyMediaContentEntityBuilder(courseId)
                .metadata(TestData.dummyContentMetadataEmbeddableBuilder(courseId)
                        .chapterId(chapterId)
                        .build())
                .build();
    }

    private AssessmentEntity createAssessmentForChapter(final UUID courseId, final UUID chapterId, final ContentType type) {
        return TestData.dummyAssessmentEntityBuilder(courseId)
                .metadata(TestData.dummyContentMetadataEmbeddableBuilder(courseId)
                        .type(type)
                        .chapterId(chapterId)
                        .build())
                .build();
    }
}
