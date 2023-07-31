package de.unistuttgart.iste.gits.content_service.service;

import de.unistuttgart.iste.gits.content_service.persistence.dao.StageEntity;
import de.unistuttgart.iste.gits.content_service.persistence.dao.WorkPathEntity;
import de.unistuttgart.iste.gits.content_service.persistence.mapper.WorkPathMapper;
import de.unistuttgart.iste.gits.content_service.persistence.repository.WorkPathRepository;
import de.unistuttgart.iste.gits.generated.dto.Stage;
import de.unistuttgart.iste.gits.generated.dto.StageOrderInput;
import de.unistuttgart.iste.gits.generated.dto.WorkPath;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.modelmapper.ModelMapper;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class WorkPathServiceTest {

    private final WorkPathMapper workPathMapper = new WorkPathMapper(new ModelMapper());
    private final WorkPathRepository workPathRepository = Mockito.mock(WorkPathRepository.class);

    private final WorkPathService workPathService = new WorkPathService(workPathMapper, workPathRepository);

    @Test
    void createWorkPath() {
    }

    @Test
    void updateWorkPath() {
    }

    @Test
    void deleteWorkPath() {
    }

    @Test
    void reorderStagesTest() {

        UUID workPathId = UUID.randomUUID();

        Set<StageEntity> stageEntities = Set.of(
                buildStageEntity(workPathId, 0),
                buildStageEntity(workPathId, 1),
                buildStageEntity(workPathId, 2),
                buildStageEntity(workPathId, 3)
        );

        WorkPathEntity workPathEntity = WorkPathEntity.builder()
                .id(workPathId)
                .name("Work-Path 1")
                .chapterId(UUID.randomUUID())
                .stages(stageEntities)
                .build();

        List<UUID> sortedStageIds = stageEntities.stream().map(stageEntity -> stageEntity.getId()).sorted().toList();


        StageOrderInput stageOrderInput = StageOrderInput.builder()
                .setWorkPathId(workPathId)
                .setStageIds(sortedStageIds)
                .build();

        //mock database
        when(workPathRepository.getReferenceById(stageOrderInput.getWorkPathId())).thenReturn(workPathEntity);
        when(workPathRepository.save(any())).thenReturn(workPathEntity);

        WorkPath result = workPathService.reorderStages(stageOrderInput);

        verify(workPathRepository, times(1)).getReferenceById(workPathId);
        verify(workPathRepository, times(1)).save(any());

        for (Stage stage: result.getStages()) {
            assertEquals(sortedStageIds.indexOf(stage.getId()), stage.getPosition());
        }
    }

    @Test
    void getWorkPathByChapterId() {
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