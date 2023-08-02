package de.unistuttgart.iste.gits.content_service.api.mutation;

import de.unistuttgart.iste.gits.common.testutil.GraphQlApiTest;
import de.unistuttgart.iste.gits.common.testutil.TablesToDelete;
import de.unistuttgart.iste.gits.content_service.persistence.dao.StageEntity;
import de.unistuttgart.iste.gits.content_service.persistence.dao.WorkPathEntity;
import de.unistuttgart.iste.gits.content_service.persistence.repository.StageRepository;
import de.unistuttgart.iste.gits.content_service.persistence.repository.WorkPathRepository;
import de.unistuttgart.iste.gits.generated.dto.Stage;
import de.unistuttgart.iste.gits.generated.dto.StageOrderInput;
import de.unistuttgart.iste.gits.generated.dto.WorkPath;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.graphql.test.tester.GraphQlTester;

import java.util.*;
import static org.junit.jupiter.api.Assertions.*;

@GraphQlApiTest
@TablesToDelete({"stage_required_contents", "stage_optional_content", "stage" ,"work_path" ,  "content_tags", "user_progress_data", "content", "tag"})
class MutationChangeStageOrderTest {

    @Autowired
    WorkPathRepository workPathRepository;

    @Autowired
    StageRepository stageRepository;

    @Test
    void testUpdateStageOrder(GraphQlTester tester){
        // set up database content and input
        List<UUID> newStageOrderList = new ArrayList<>();

        WorkPathEntity workPathEntity = WorkPathEntity.builder()
                .name("WorkPath test")
                .chapterId(UUID.randomUUID())
                .stages(new HashSet<>())
                .build();

        workPathEntity = workPathRepository.save(workPathEntity);

        Set<StageEntity> stageEntitySet = Set.of(
                buildStageEntity(workPathEntity.getId(), 0),
                buildStageEntity(workPathEntity.getId(), 1),
                buildStageEntity(workPathEntity.getId(), 2)
        );

        workPathEntity.setStages(stageEntitySet);

        workPathEntity = workPathRepository.save(workPathEntity);


        // reorder by putting the last element at the beginning and shifting each following element by one index
        for (int i = 0; i < 3; i++) {
            for (StageEntity stageEntity: workPathEntity.getStages()) {
                if (stageEntity.getPosition() % 3 == i){
                    newStageOrderList.add(stageEntity.getId());
                }
            }

        }

        StageOrderInput input = StageOrderInput.builder()
                .setWorkPathId(workPathEntity.getId())
                .setStageIds(newStageOrderList)
                .build();

        String query = """
                mutation($input: StageOrderInput!){
                    updateStageOrder(input: $input){
                        id
                        chapterId
                        name
                        stages {
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
        tester.document(query).variable("input", input).execute().path("updateStageOrder").entity(WorkPath.class).satisfies(workPath -> {
            assertEquals(3, workPath.getStages().size());
            for (Stage stage: workPath.getStages()) {
                assertEquals(newStageOrderList.indexOf(stage.getId()), stage.getPosition());
            }
                }
        );
    }

    private StageEntity buildStageEntity (UUID workPathId, int pos){
        return StageEntity.builder()
                .id(UUID.randomUUID())
                .workPathId(workPathId)
                .position(pos)
                .requiredContents(new HashSet<>())
                .optionalContent(new HashSet<>())
                .build();
    }
}
