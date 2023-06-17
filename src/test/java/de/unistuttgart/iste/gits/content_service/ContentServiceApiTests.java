package de.unistuttgart.iste.gits.content_service;

import de.unistuttgart.iste.gits.common.testutil.GitsPostgresSqlContainer;
import org.junit.ClassRule;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.graphql.tester.AutoConfigureGraphQlTester;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.graphql.test.tester.GraphQlTester;
import org.springframework.test.context.ActiveProfiles;
import org.testcontainers.containers.PostgreSQLContainer;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;

@SpringBootTest(classes = ContentServiceApplication.class)
@AutoConfigureGraphQlTester
@ActiveProfiles("test")
class ContentServiceApiTests {
    @Autowired
    private GraphQlTester graphQlTester;

    @ClassRule
    public static PostgreSQLContainer<GitsPostgresSqlContainer> postgreSQLContainer = GitsPostgresSqlContainer.getInstance();

    @Test
    void shouldAddContentAndQueryBack() {
        GraphQlTester.Response response = graphQlTester.documentName("create-content")
                .execute();
        response
                .path("createContent.name").entity(String.class).isEqualTo("New Content")
                .path("createContent.rewardPoints").entity(Integer.class).isEqualTo(5)
                .path("createContent.tagNames").entity(List.class).isEqualTo(List.of("Tag1"))
                .path("createContent.workedOn").entity(Boolean.class).isEqualTo(Boolean.FALSE)
        ;
    }

    @Test
    void shouldAddContentAndAddTagAfterwardsAndQueryBack() {
        // create same content as in shouldAddContentAndQueryBack
        // only store content id to add a new tag, the other parts are verified in other tests.
        final String tagName = "Tag2";
        UUID contentId = graphQlTester.documentName("create-content")
                         .execute().path("createContent.id").entity(UUID.class).get();
        GraphQlTester.Response response = graphQlTester.documentName("add-tag-to-content")
                .variable("uuid", contentId)
                .variable("tag", tagName)
                .execute();
        List tagNameList = response.path("addTagToContent.tagNames").entity(List.class).get();
        assertThat(tagNameList.contains("Tag1"), equalTo(true));
        assertThat(tagNameList.contains("Tag2"), equalTo(true));
    }

    @Test
    void shouldAddContentAndDeleteTagAfterwardsAndQueryBack() {
        // create same content as in shouldAddContentAndQueryBack
        // only store content id to add a new tag, the other parts are verified in other tests.
        final String tagName = "Tag1";
        UUID contentId = graphQlTester.documentName("create-content")
                .execute().path("createContent.id").entity(UUID.class).get();
        GraphQlTester.Response response = graphQlTester.documentName("remove-tag-from-content")
                .variable("uuid", contentId)
                .variable("tag", tagName)
                .execute();
        List tagNameList = response.path("removeTagFromContent.tagNames").entity(List.class).get();
        assertThat(tagNameList.size(), equalTo(0));
    }

    @Test
    void shouldAddContentAndGetById() {
        UUID contentId = graphQlTester.documentName("create-content")
                .execute().path("createContent.id").entity(UUID.class).get();
        List<UUID> ids = new ArrayList<>();
        ids.add(contentId);
        GraphQlTester.Response response = graphQlTester.documentName("get-contents-by-id")
                .variable("ids", ids)
                .execute();
        List responseList = response.path("contentsById").entity(List.class).get();
        assertThat(responseList.size(), equalTo(1));
        response.path("contentsById[0].id").entity(UUID.class).isEqualTo(contentId);
    }

    @Test
    void shouldUpdateContent() {
        UUID contentId = graphQlTester.documentName("create-content")
                .execute().path("createContent.id").entity(UUID.class).get();
        UUID chapterId = graphQlTester.documentName("create-content")
                .execute().path("createContent.chapterId").entity(UUID.class).get();
        GraphQlTester.Response response = graphQlTester.documentName("update-content")
                .variable("uuid_content", contentId)
                .variable("uuid_chapter", chapterId)
                .execute();
        response
                .path("updateContent.name").entity(String.class).isEqualTo("Updated Content")
                .path("updateContent.rewardPoints").entity(Integer.class).isEqualTo(6)
                .path("updateContent.tagNames").entity(List.class).isEqualTo(List.of("UpdatedTag1"))
                .path("updateContent.workedOn").entity(Boolean.class).isEqualTo(Boolean.TRUE);

    }
}
