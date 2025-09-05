package de.unistuttgart.iste.meitrex.content_service.client;

import de.unistuttgart.iste.meitrex.content_service.exception.ContentServiceConnectionException;
import de.unistuttgart.iste.meitrex.content_service.persistence.entity.ItemEntity;
import de.unistuttgart.iste.meitrex.generated.dto.*;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.Converter;
import org.modelmapper.ModelMapper;
import org.springframework.graphql.client.ClientGraphQlResponse;
import org.springframework.graphql.client.GraphQlClient;
import reactor.core.publisher.SynchronousSink;

import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * Client for the content service, allowing to query contents with user progress data.
 */
@Slf4j
public class ContentServiceClient {

    private static final long RETRY_COUNT = 3;
    private final GraphQlClient graphQlClient;
    private final ModelMapper modelMapper;

    public ContentServiceClient(final GraphQlClient graphQlClient) {
        this.graphQlClient = graphQlClient;
        this.modelMapper = new ModelMapper();
        modelMapper.typeMap(String.class, OffsetDateTime.class).setConverter(stringToOffsetDateTimeConverter());
    }

    /**
     * Queries the content service for all contents of the given chapter.
     *
     * @param userId    the id of the user for which to query the progress data
     * @param chapterId the id of the chapter for which to query the contents
     * @return a list of contents of the given chapter
     * @throws ContentServiceConnectionException if the connection to the content
     *                                           service fails or any other error occurs
     */
    public List<Content> queryContentsOfChapter(final UUID userId, final UUID chapterId) throws ContentServiceConnectionException {
        log.info("Querying contents of chapter with id {}", chapterId);

        try {
            return graphQlClient.document(QueryDefinitions.CONTENTS_BY_CHAPTER_IDS_QUERY)
                    .variable("chapterIds", List.of(chapterId))
                    .variable("userId", userId)
                    .execute()
                    .handle((ClientGraphQlResponse result, SynchronousSink<List<Content>> sink)
                            -> handleContentServiceResponse(result, sink, QueryDefinitions.CONTENTS_BY_CHAPTER_ID_QUERY_NAME))
                    .retry(RETRY_COUNT)
                    .block();
        } catch (final RuntimeException e) {
            unwrapContentServiceConnectionException(e);
        }
        return List.of(); // unreachable
    }


    /**
     * Queries the content service for all contents of the given course.
     *
     * @param userId   the id of the user for which to query the progress data
     * @param courseId the id of the course for which to query the contents
     * @return a list of contents of the given course
     * @throws ContentServiceConnectionException if the connection to the content
     *                                           service fails or any other error occurs
     */
    public List<Content> queryContentsOfCourse(final UUID userId, final UUID courseId) throws ContentServiceConnectionException {
        log.info("Querying contents of course with id {}", courseId);

        try {
            return graphQlClient.document(QueryDefinitions.CONTENTS_BY_COURSE_IDS_QUERY)
                    .variable("courseIds", List.of(courseId))
                    .variable("userId", userId)
                    .execute()
                    .handle((ClientGraphQlResponse result, SynchronousSink<List<Content>> sink)
                            -> handleContentServiceResponse(result, sink, QueryDefinitions.CONTENTS_BY_COURSE_ID_QUERY_NAME))
                    .retry(RETRY_COUNT)
                    .block();
        } catch (final RuntimeException e) {
            unwrapContentServiceConnectionException(e);
        }
        return List.of(); // unreachable
    }

    /**
     * Queries the content service for all contents of the given chapter.
     *
     * @param userId    the id of the user for which to query the progress data
     * @param contentIds the ids of the content
     * @return List of content with the given ids
     * @throws ContentServiceConnectionException if the connection to the content
     *                                           service fails or any other error occurs
     */
    public List<Content> queryContentsByIds(final UUID userId, final List<UUID> contentIds) throws ContentServiceConnectionException {
        log.info("Querying content with ids {}", contentIds);


        return graphQlClient.document(QueryDefinitions.CONTENTS_BY_CONTENT_IDS_QUERY)
                .variable("ids", contentIds)
                .variable("userId", userId)
                .execute()
                .handle((ClientGraphQlResponse result, SynchronousSink<List<Content>> sink)
                            -> handleContentServiceResponseContent(result, sink, QueryDefinitions.CONTENTS_BY_CONTENT_IDS_QUERY_NAME))
                .retry(RETRY_COUNT)
                .block();
    }

    /**
     * Queries the content service for the progress of the given chapter.
     *
     * @param userId    the id of the user for which to query the progress data
     * @param chapterId the id of the chapter
     * @return List of content with the given ids
     * @throws ContentServiceConnectionException if the connection to the content
     *                                           service fails or any other error occurs
     */
    public CompositeProgressInformation queryProgressByChapterId(final UUID userId, final UUID chapterId) throws ContentServiceConnectionException {
        log.info("Querying chapter progress with for chapter with the id {}", chapterId);
        return graphQlClient.document(QueryDefinitions.PROGRESS_BY_CHAPTER_ID)
                .variable("chapterId", chapterId)
                .variable("userId", userId)
                .retrieveSync(QueryDefinitions.PROGRESS_BY_CHAPTER_ID_QUERY_NAME)
                .toEntity(CompositeProgressInformation.class);
    }

    /**
     * Queries the content service for all content Ids of the given course.
     * @param courseId the id of the course for which to query the contents
     * @return a list of UUIDs of the contents in the given course
     * @throws ContentServiceConnectionException if the connection to the content
     *                                           service fails or any other error occurs
     */
    public List<UUID> queryContentIdsOfCourse(final UUID courseId) throws ContentServiceConnectionException {
        log.info("Querying content Ids of course with id {}", courseId);

        try {
            return graphQlClient.document(QueryDefinitions.CONTENT_IDS_BY_COURSE_IDS_QUERY)
                    .variable("courseIds", List.of(courseId))
                    .execute()
                    .handle((ClientGraphQlResponse result, SynchronousSink<List<UUID>> sink) -> {
                        try {
                            List<UUID> ids = convertResponseToListOfUUIDs(result, QueryDefinitions.CONTENTS_BY_COURSE_ID_QUERY_NAME);
                            sink.next(ids);
                            sink.complete();
                        } catch (Exception e) {
                            sink.error(e);
                        }
                    })
                    .retry(RETRY_COUNT)
                    .block();
        } catch (final RuntimeException e) {
            unwrapContentServiceConnectionException(e);
        }
        return List.of(); // unreachable
    }

    public List<Section> querySectionsOfCourse(final UUID courseId, final UUID userId) throws ContentServiceConnectionException {
        log.info("Querying sections of course with id {}", courseId);

        try {
            return graphQlClient.document(QueryDefinitions.SECTIONS_BY_COURSE_ID_QUERY)
                    .variable("courseId", courseId)
                    .variable("userId", userId)
                    .execute()
                    .handle((ClientGraphQlResponse result, SynchronousSink<List<Section>> sink) ->
                            handleContentServiceResponseSections(result, sink, "query"))
                    .retry(RETRY_COUNT)
                    .block();
        } catch (final RuntimeException e) {
            unwrapContentServiceConnectionException(e);
        }
        return List.of(); // unreachable
    }

    private static void unwrapContentServiceConnectionException(final RuntimeException e) throws ContentServiceConnectionException {
        // block wraps exceptions in a RuntimeException, so we need to unwrap them
        if (e.getCause() instanceof final ContentServiceConnectionException contentServiceConnectionException) {
            throw contentServiceConnectionException;
        }
        // if the exception is not a ContentServiceConnectionException, we don't know how to handle it
        throw e;
    }

    private void handleContentServiceResponseSections(final ClientGraphQlResponse result,
                                                      final SynchronousSink<List<Section>> sink,
                                                      final String queryName) {
        if(!result.isValid()) {
            sink.error(new ContentServiceConnectionException(
                    "Error while fetching contents from content service: Invalid response.",
                    result.getErrors()));
            return;
        }

        List<Section> fields = result.field(queryName).toEntityList(Section.class);

        final List<Section> retrievedSections = new ArrayList<>(fields);

        sink.next(retrievedSections);
    }

    private void handleContentServiceResponseContent(final ClientGraphQlResponse result,
                                                     final SynchronousSink <List<Content>> sink,
                                                     final String queryName){
        log.info(result.toString());
        if (!result.isValid()) {
            sink.error(new ContentServiceConnectionException(
                    "Error while fetching contents from content service: Invalid response.",
                    result.getErrors()));
            return;
        }
        final List<Content> retrievedContents;
        try {
            retrievedContents = convertResponseContentToListOfContent(result, queryName);
        } catch (final ContentServiceConnectionException e) {
            sink.error(e);
            return;
        }

        sink.next(retrievedContents);
    }

    private List<Content> convertResponseContentToListOfContent(final ClientGraphQlResponse result,
                                                         final String queryName)
            throws ContentServiceConnectionException {
        return result.field(queryName).toEntityList(Content.class);
    }

    private void handleContentServiceResponse(final ClientGraphQlResponse result,
                                              final SynchronousSink<List<Content>> sink,
                                              final String queryName) {
        log.info(result.toString());
        if (!result.isValid()) {
            sink.error(new ContentServiceConnectionException(
                    "Error while fetching contents from content service: Invalid response.",
                    result.getErrors()));
            return;
        }

        final List<Content> retrievedContents;
        try {
            retrievedContents = convertResponseToListOfContent(result, queryName);
        } catch (final ContentServiceConnectionException e) {
            sink.error(e);
            return;
        }

        sink.next(retrievedContents);
    }

    private List<Content> convertResponseToListOfContent(final ClientGraphQlResponse result,
                                                         final String queryName)
            throws ContentServiceConnectionException {

        return result.field(queryName + "[0]").toEntityList(Content.class);
    }

    private List<UUID> convertResponseToListOfUUIDs(final ClientGraphQlResponse result, final String queryName)
            throws ContentServiceConnectionException {
        // Extract the list of id maps from the response
        final List<Map<String, String>> contentIdMaps = result.field(queryName + "[0]").getValue();

        if (contentIdMaps == null) {
            throw new ContentServiceConnectionException(
                    "Error while fetching contents from content service: Missing field in response.");
        }

        // Convert each map to UUID by reading the "id" field
        return contentIdMaps.stream()
                .map(map -> UUID.fromString(map.get("id")))
                .toList();
    }

    private Converter<String, OffsetDateTime> stringToOffsetDateTimeConverter() {
        return context -> {
            final String source = context.getSource();
            if (source == null) {
                return null;
            }
            return OffsetDateTime.parse(source);
        };
    }
}
