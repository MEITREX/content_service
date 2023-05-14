package de.unistuttgart.iste.gits.content;

import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.graphql.tester.AutoConfigureGraphQlTester;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.graphql.test.tester.GraphQlTester;
import org.springframework.test.context.junit4.SpringRunner;

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
                .path("createContent.contentName").entity(String.class).isEqualTo("New Content")
                .path("createContent.rewardPoints").entity(Integer.class).isEqualTo(5)
                .path("createContent.workedOn").entity(Boolean.class).isEqualTo(Boolean.FALSE);
    }

    @Test
    void shouldAddContentWithTagAndQueryBack() {
        // create same content as in shouldAddContentAndQueryBack
        // only story content Id, the other parts are verified in other tests.
        UUID contentId = this.graphQlTester.documentName("create-content")
                         .execute().path("createContent.id").entity(UUID.class).get();
        this.graphQlTester.documentName("create-tag")
                .variable("contentId", contentId)
                .execute()
                .path("createTag.name").entity(String.class).isEqualTo("Tag1");
//        this.graphQlTester.documentName("get-contents-by-tag")
//                .variable("tag", "Tag1")
//                .execute()
//                .path("getContentsByTag.id").entity(UUID.class).isEqualTo(contentId);
    }

}
