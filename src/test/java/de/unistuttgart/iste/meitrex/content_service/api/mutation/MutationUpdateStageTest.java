package de.unistuttgart.iste.meitrex.content_service.api.mutation;

import de.unistuttgart.iste.meitrex.common.testutil.*;
import de.unistuttgart.iste.meitrex.common.user_handling.LoggedInUser;
import de.unistuttgart.iste.meitrex.common.user_handling.LoggedInUser.UserRoleInCourse;
import de.unistuttgart.iste.meitrex.content_service.persistence.entity.*;
import de.unistuttgart.iste.meitrex.content_service.persistence.repository.*;

import de.unistuttgart.iste.meitrex.generated.dto.UpdateStageInput;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.graphql.test.tester.GraphQlTester;
import org.springframework.test.annotation.Commit;

import java.util.*;

import static de.unistuttgart.iste.meitrex.common.testutil.TestUsers.userWithMembershipInCourseWithId;
import static de.unistuttgart.iste.meitrex.content_service.TestData.buildContentEntity;


@GraphQlApiTest
class MutationUpdateStageTest {

    @Autowired
    private SectionRepository sectionRepository;

    @Autowired
    private StageRepository stageRepository;

    @Autowired
    private ContentRepository contentRepository;


    private final UUID courseId = UUID.randomUUID();

    @InjectCurrentUserHeader
    private final LoggedInUser loggedInUser = userWithMembershipInCourseWithId(courseId, UserRoleInCourse.ADMINISTRATOR);

    @Test
    @Transactional
    @Commit
    void testUpdateStage(final GraphQlTester tester) {
        final List<UUID> contentIds = new ArrayList<>();
        SectionEntity sectionEntity = SectionEntity.builder()
                .name("Test Section")
                .chapterId(UUID.randomUUID())
                .courseId(courseId)
                .stages(new HashSet<>())
                .build();
        sectionEntity = sectionRepository.save(sectionEntity);

        StageEntity stageEntity = StageEntity.builder()
                .sectionId(sectionEntity.getId())
                .position(0)
                .optionalContents(new HashSet<>())
                .requiredContents(new HashSet<>())
                .build();
        stageEntity = stageRepository.save(stageEntity);


        for (int i = 0; i < 2; i++) {
            MediaContentEntity entity = buildContentEntity(sectionEntity.getChapterId());
            entity = contentRepository.save(entity);
            contentIds.add(entity.getId());
        }

        final UpdateStageInput input = UpdateStageInput.builder()
                .setId(stageEntity.getId())
                .setRequiredContents(List.of(contentIds.get(0)))
                .setOptionalContents(List.of(contentIds.get(1)))
                .build();

        final String query =
                """
                mutation ($id: UUID!, $input: UpdateStageInput!){
                    mutateSection(sectionId: $id){
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
                }
                """ ;

        final String expectedJson =
                """
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
                .variable("id", sectionEntity.getId())
                .variable("input", input)
                .execute().path("mutateSection.updateStage").matchesJson(expectedJson);

    }

    @Test
    void testTryAddContentToMultipleStages(final GraphQlTester tester) {
        final SectionEntity sectionEntity = SectionEntity.builder()
                .name("Test Section")
                .chapterId(UUID.randomUUID())
                .courseId(courseId)
                .stages(new HashSet<>())
                .build();
        sectionRepository.save(sectionEntity);

        final StageEntity stage1 = StageEntity.builder()
                .sectionId(sectionEntity.getId())
                .position(0)
                .optionalContents(new HashSet<>())
                .requiredContents(new HashSet<>())
                .build();
        stageRepository.save(stage1);

        final StageEntity stage2 = StageEntity.builder()
                .sectionId(sectionEntity.getId())
                .position(1)
                .optionalContents(new HashSet<>())
                .requiredContents(new HashSet<>())
                .build();
        stageRepository.save(stage2);

        MediaContentEntity content1 = buildContentEntity(sectionEntity.getChapterId());
        MediaContentEntity content2 = buildContentEntity(sectionEntity.getChapterId());
        content1 = contentRepository.save(content1);
        content2 = contentRepository.save(content2);

        final UpdateStageInput input1 = UpdateStageInput.builder()
                .setId(stage1.getId())
                .setRequiredContents(List.of(content1.getId()))
                .setOptionalContents(List.of(content2.getId()))
                .build();

        final String query1 =
                """
                mutation ($sectionId: UUID!, $input: UpdateStageInput!) {
                    mutateSection(sectionId: $sectionId){
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
                }
                """ ;
        tester.document(query1)
                .variable("sectionId", sectionEntity.getId())
                .variable("input", input1)
                .execute()
                .path("mutateSection.updateStage.id").entity(UUID.class).isEqualTo(stage1.getId())
                .path("mutateSection.updateStage.requiredContents[0].id").entity(UUID.class).isEqualTo(content1.getId())
                .path("mutateSection.updateStage.optionalContents[0].id").entity(UUID.class).isEqualTo(content2.getId());

        // try to add the same content to another stage. This should fail silently, the content should not be added to
        // the second stage

        final UpdateStageInput input2 = UpdateStageInput.builder()
                .setId(stage2.getId())
                .setRequiredContents(List.of(content1.getId()))
                .setOptionalContents(List.of(content2.getId()))
                .build();

        final String query2 =
                """
                mutation ($sectionId: UUID!, $input: UpdateStageInput!) {
                    mutateSection(sectionId: $sectionId){
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
                }
                """;

        tester.document(query2)
                .variable("sectionId", sectionEntity.getId())
                .variable("input", input2)
                .execute()
                .path("mutateSection.updateStage.id").entity(UUID.class).isEqualTo(stage2.getId())
                .path("mutateSection.updateStage.requiredContents").entityList(UUID.class).hasSize(0)
                .path("mutateSection.updateStage.optionalContents").entityList(UUID.class).hasSize(0);
    }

}
