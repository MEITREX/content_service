package de.unistuttgart.iste.gits.content;

import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.graphql.tester.AutoConfigureGraphQlTester;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.graphql.test.tester.GraphQlTester;
import org.springframework.test.context.junit4.SpringRunner;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;

import java.util.List;
import java.util.UUID;

@SpringBootTest(classes=ContentServiceApplication.class)
@AutoConfigureGraphQlTester
public class ContentServiceApiTests {
    @Autowired
    private GraphQlTester graphQlTester;
    @Test
    void shouldAddContentAndQueryBack() {
        this.graphQlTester.documentName("create-content")
                .execute()
                .path("createContent.name").entity(String.class).isEqualTo("New Content")
                .path("createContent.rewardPoints").entity(Integer.class).isEqualTo(5)
                .path("createContent.workedOn").entity(Boolean.class).isEqualTo(Boolean.FALSE);
    }

    @Test
    void shouldAddContentWithTagAndQueryBack() {
        // create same content as in shouldAddContentAndQueryBack
        // only store content Id, the other parts are verified in other tests.
        final String tagName = "Tag1";
        UUID contentId = this.graphQlTester.documentName("create-content")
                         .execute().path("createContent.id").entity(UUID.class).get();
        UUID tagId = this.graphQlTester.documentName("create-tag")
                .variable("contentId", contentId)
                .variable("tagName", tagName)
                .execute()
                .path("createTag.name").entity(String.class).isEqualTo("Tag1")
                .path("createTag.id").entity(UUID.class).get();
        this.graphQlTester.documentName("get-content-by-tag-id")
                .variable("tagId", tagId)
                .execute()
                .path("contentByTag.id").entity(UUID.class).isEqualTo(contentId);
        GraphQlTester.Response response = this.graphQlTester.documentName("get-contents-by-tag-name")
                .variable("tag", tagName)
                .execute();
        List responseList = response.path("contentsByTagName").entity(List.class).get();
        assertThat(responseList.size(),equalTo(1));
        response.path("contentsByTagName[0].id").entity(UUID.class).isEqualTo(contentId);
    }

}
