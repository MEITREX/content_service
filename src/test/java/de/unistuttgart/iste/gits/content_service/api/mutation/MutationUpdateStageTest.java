package de.unistuttgart.iste.gits.content_service.api.mutation;

import de.unistuttgart.iste.gits.common.testutil.GraphQlApiTest;
import de.unistuttgart.iste.gits.common.testutil.TablesToDelete;
import de.unistuttgart.iste.gits.content_service.persistence.dao.*;
import de.unistuttgart.iste.gits.content_service.persistence.repository.ContentRepository;
import de.unistuttgart.iste.gits.content_service.persistence.repository.StageRepository;
import de.unistuttgart.iste.gits.content_service.persistence.repository.WorkPathRepository;
import de.unistuttgart.iste.gits.generated.dto.ContentType;
import de.unistuttgart.iste.gits.generated.dto.UpdateStageInput;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.graphql.test.tester.GraphQlTester;
import org.springframework.test.annotation.Commit;

import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.UUID;

@GraphQlApiTest
@TablesToDelete({"stage_required_contents", "stage_optional_content", "stage" ,"work_path" ,  "content_tags", "user_progress_data", "content", "tag"})
class MutationUpdateStageTest {

    @Autowired
    private WorkPathRepository workPathRepository;

    @Autowired
    private StageRepository stageRepository;

    @Autowired
    private ContentRepository contentRepository;

    @Test
    @Transactional
    @Commit
    void testUpdateStage(GraphQlTester tester){
        List<UUID> contentIds = new ArrayList<>();
        WorkPathEntity workPathEntity = WorkPathEntity.builder()
                .name("Test Work-Path")
                .chapterId(UUID.randomUUID())
                .stages(new HashSet<>())
                .build();
        workPathEntity = workPathRepository.save(workPathEntity);

        StageEntity stageEntity = StageEntity.builder()
                .workPathId(workPathEntity.getId())
                .position(0)
                .optionalContent(new HashSet<>())
                .requiredContents(new HashSet<>())
                .build();
        stageEntity = stageRepository.save(stageEntity);


        for (int i = 0; i < 2; i++) {
            MediaContentEntity entity = buildContentEntity(workPathEntity.getChapterId());
            entity = contentRepository.save(entity);
            contentIds.add(entity.getId());
        }

        UpdateStageInput input = UpdateStageInput.builder()
                .setId(stageEntity.getId())
                .setRequiredContents(List.of(contentIds.get(0)))
                .setOptionalContents(List.of(contentIds.get(1)))
                .build();

        String query = """
                mutation ($input: UpdateStageInput!){
                    updateStage(input: $input){
                        id
                        position
                        requiredContents {
                            id
                        }
                        optionalContents {
                            id
                        }
                    }
                }
                """;

        String expectedJson = """
                      {
                      "id": "%s",
                      "position": 0,
                      "requiredContents": [
                        {
                          "id": "%s"
                        }
                      ],
                      "optionalContents": [
                        {
                          "id": "%s"
                        }
                      ]
                    }
                """.formatted(stageEntity.getId(), contentIds.get(0), contentIds.get(1));

        tester.document(query)
                .variable("input", input)
                .execute().path("updateStage").matchesJson(expectedJson);

    }

    private MediaContentEntity buildContentEntity(UUID chapterId){
        return MediaContentEntity.builder()
                .id(UUID.randomUUID())
                .metadata(
                        ContentMetadataEmbeddable.builder()
                                .tags(new HashSet<>())
                                .name("Test")
                                .type(ContentType.MEDIA)
                                .suggestedDate(OffsetDateTime.parse("2020-01-01T00:00:00.000Z"))
                                .rewardPoints(20)
                                .chapterId(chapterId)
                                .build()
                ).build();
    }
}
