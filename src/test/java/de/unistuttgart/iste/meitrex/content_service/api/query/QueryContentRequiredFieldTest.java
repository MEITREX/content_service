package de.unistuttgart.iste.meitrex.content_service.api.query;

import de.unistuttgart.iste.meitrex.common.testutil.GraphQlApiTest;
import de.unistuttgart.iste.meitrex.content_service.persistence.entity.ContentEntity;
import de.unistuttgart.iste.meitrex.content_service.persistence.entity.MediaContentEntity;
import de.unistuttgart.iste.meitrex.content_service.persistence.entity.SectionEntity;
import de.unistuttgart.iste.meitrex.content_service.persistence.entity.StageEntity;
import de.unistuttgart.iste.meitrex.content_service.persistence.repository.ContentRepository;
import de.unistuttgart.iste.meitrex.content_service.persistence.repository.SectionRepository;
import de.unistuttgart.iste.meitrex.content_service.persistence.repository.StageRepository;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.graphql.test.tester.GraphQlTester;
import org.springframework.test.annotation.Commit;

import java.util.*;

import static de.unistuttgart.iste.meitrex.content_service.TestData.buildContentEntity;

@GraphQlApiTest
public class QueryContentRequiredFieldTest {
    @Autowired
    private SectionRepository sectionRepository;
    @Autowired
    private StageRepository stageRepository;
    @Autowired
    private ContentRepository contentRepository;

    private final UUID courseId = UUID.randomUUID();

    @Test
    @Transactional
    @Commit
    void testQueryContentRequiredField(GraphQlTester tester) {
        final List<ContentEntity> contentEntities = new ArrayList<>();
        SectionEntity sectionEntity = SectionEntity.builder()
                .name("Test Section")
                .chapterId(UUID.randomUUID())
                .courseId(courseId)
                .stages(new HashSet<>())
                .build();
        sectionEntity = sectionRepository.save(sectionEntity);

        for (int i = 0; i < 2; i++) {
            MediaContentEntity entity = buildContentEntity(sectionEntity.getChapterId());
            entity = contentRepository.save(entity);
            contentEntities.add(entity);
        }

        StageEntity stageEntity = StageEntity.builder()
                .sectionId(sectionEntity.getId())
                .position(0)
                .requiredContents(Set.of(contentEntities.get(0)))
                .optionalContents(Set.of(contentEntities.get(1)))
                .build();
        stageEntity = stageRepository.save(stageEntity);

        String query =
                """
                query($contentIds: [UUID!]!) {
                    _internal_noauth_contentsByIds(ids: $contentIds) {
                        id
                        required
                    }
                }
                """;

        tester.document(query)
                .variable("contentIds", contentEntities.stream().map(ContentEntity::getId).toList())
                .execute()
                .path("_internal_noauth_contentsByIds[0].id")
                    .entity(UUID.class).isEqualTo(contentEntities.get(0).getId())
                .path("_internal_noauth_contentsByIds[0].required")
                    .entity(Boolean.class).isEqualTo(true)
                .path("_internal_noauth_contentsByIds[1].id")
                    .entity(UUID.class).isEqualTo(contentEntities.get(1).getId())
                .path("_internal_noauth_contentsByIds[1].required")
                    .entity(Boolean.class).isEqualTo(false);
    }
}
