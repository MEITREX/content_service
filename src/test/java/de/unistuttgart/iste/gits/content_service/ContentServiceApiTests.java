package de.unistuttgart.iste.gits.content_service;

import de.unistuttgart.iste.gits.common.testutil.GraphQlApiTest;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.graphql.test.tester.GraphQlTester;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@GraphQlApiTest
public class ContentServiceApiTests {
    @Test
    void shouldAddContentAndQueryBack(GraphQlTester graphQlTester) {
        graphQlTester.documentName("create-content")
                .execute()
                .path("createContent.name").entity(String.class).isEqualTo("New Content")
                .path("createContent.rewardPoints").entity(Integer.class).isEqualTo(5)
                .path("createContent.workedOn").entity(Boolean.class).isEqualTo(Boolean.FALSE);
    }

    @Test
    @Disabled // disabled until proper tag system is implemented
    void shouldAddContentWithTagAndQueryBack(GraphQlTester graphQlTester) {
        // create same content as in shouldAddContentAndQueryBack
        // only store content id, the other parts are verified in other tests.
        final String tagName = "Tag1";
        UUID contentId = graphQlTester.documentName("create-content")
                         .execute().path("createContent.id").entity(UUID.class).get();
        UUID tagId = graphQlTester.documentName("create-tag")
                .variable("contentId", contentId)
                .variable("tagName", tagName)
                .execute()
                .path("createTag.name").entity(String.class).isEqualTo("Tag1")
                .path("createTag.id").entity(UUID.class).get();
        graphQlTester.documentName("get-content-by-tag-id")
                .variable("tagId", tagId)
                .execute()
                .path("contentByTag.id").entity(UUID.class).isEqualTo(contentId);
        GraphQlTester.Response response = graphQlTester.documentName("get-contents-by-tag-name")
                .variable("tag", tagName)
                .execute();
        List responseList = response.path("contentsByTagName").entity(List.class).get();
        assertThat(responseList.size(),equalTo(1));
        response.path("contentsByTagName[0].id").entity(UUID.class).isEqualTo(contentId);
    }

    @Test
    void shouldAddContentAndGetById(GraphQlTester graphQlTester) {
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
}
