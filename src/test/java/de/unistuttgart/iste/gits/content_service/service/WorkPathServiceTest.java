package de.unistuttgart.iste.gits.content_service.service;

import de.unistuttgart.iste.gits.content_service.persistence.dao.StageEntity;
import de.unistuttgart.iste.gits.content_service.persistence.dao.WorkPathEntity;
import de.unistuttgart.iste.gits.content_service.persistence.mapper.ContentMapper;
import de.unistuttgart.iste.gits.content_service.persistence.mapper.StageMapper;
import de.unistuttgart.iste.gits.content_service.persistence.mapper.WorkPathMapper;
import de.unistuttgart.iste.gits.content_service.persistence.repository.WorkPathRepository;
import de.unistuttgart.iste.gits.generated.dto.*;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.modelmapper.ModelMapper;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class WorkPathServiceTest {

    private final StageMapper stageMapper = new StageMapper(new ContentMapper(new ModelMapper()));
    private final WorkPathMapper workPathMapper = new WorkPathMapper(stageMapper);
    private final WorkPathRepository workPathRepository = Mockito.mock(WorkPathRepository.class);

    private final WorkPathService workPathService = new WorkPathService(workPathMapper, workPathRepository);

    @Test
    void createWorkPath() {
        //init
        CreateWorkPathInput input = CreateWorkPathInput.builder()
                .setChapterId(UUID.randomUUID())
                .setName("Test Work-Path")
                .build();

        WorkPathEntity workPathEntity = WorkPathEntity.builder()
                .name(input.getName())
                .id(UUID.randomUUID())
                .chapterId(input.getChapterId())
                .stages(new HashSet<>()).build();

        WorkPath expectedResult = WorkPath.builder()
                .setId(workPathEntity.getId())
                .setName(workPathEntity.getName())
                .setChapterId(workPathEntity.getChapterId())
                .setStages(new ArrayList<>())
                .build();

        //mock database
        when(workPathRepository.save(any())).thenReturn(workPathEntity);

        // execute method under test
        WorkPath result = workPathService.createWorkPath(input);

        assertEquals(expectedResult, result);
        assertEquals(expectedResult.getId(), result.getId());
        assertEquals(expectedResult.getName(), result.getName());
        assertEquals(expectedResult.getChapterId(), result.getChapterId());
        assertEquals(expectedResult.getStages(), result.getStages());
    }

    @Test
    void updateWorkPath() {
        UUID workPathId = UUID.randomUUID();
        UpdateWorkPathInput input = UpdateWorkPathInput.builder()
                .setId(workPathId)
                .setName("Test Work-Path")
                .build();

        WorkPathEntity oldWorkPathEntity = WorkPathEntity.builder()
                .name("This is a work path")
                .id(workPathId)
                .chapterId(UUID.randomUUID())
                .stages(new HashSet<>()).build();

        WorkPathEntity newWorkPathEntity = WorkPathEntity.builder()
                .name(input.getName())
                .id(workPathId)
                .chapterId(oldWorkPathEntity.getChapterId())
                .stages(new HashSet<>()).build();

        WorkPath expectedResult = WorkPath.builder()
                .setId(newWorkPathEntity.getId())
                .setName(newWorkPathEntity.getName())
                .setChapterId(newWorkPathEntity.getChapterId())
                .setStages(new ArrayList<>())
                .build();

        //mock database
        when(workPathRepository.existsById(input.getId())).thenReturn(true);
        when(workPathRepository.getReferenceById(input.getId())).thenReturn(oldWorkPathEntity);
        when(workPathRepository.save(any())).thenReturn(newWorkPathEntity);

        // execute method under test
        WorkPath result = workPathService.updateWorkPath(input);

        verify(workPathRepository, times(1)).save(newWorkPathEntity);

        assertEquals(expectedResult, result);
        assertEquals(expectedResult.getId(), result.getId());
        assertEquals(expectedResult.getName(), result.getName());
        assertEquals(expectedResult.getChapterId(), result.getChapterId());
        assertEquals(expectedResult.getStages(), result.getStages());
    }

    @Test
    void updateWorkInvalidIdPath() {
        UUID workPathId = UUID.randomUUID();
        UpdateWorkPathInput input = UpdateWorkPathInput.builder()
                .setId(workPathId)
                .setName("Test Work-Path")
                .build();

        //mock database
        when(workPathRepository.existsById(input.getId())).thenReturn(false);

        // execute method under test
        assertThrows(EntityNotFoundException.class, () -> workPathService.updateWorkPath(input));
    }

    // case: update Work path with existing Stages
    @Test
    void updateWorkWithStagesPath() {
        UUID workPathId = UUID.randomUUID();
        UpdateWorkPathInput input = UpdateWorkPathInput.builder()
                .setId(workPathId)
                .setName("Test Work-Path")
                .build();

        WorkPathEntity oldWorkPathEntity = WorkPathEntity.builder()
                .name("This is a work path")
                .id(workPathId)
                .chapterId(UUID.randomUUID())
                .stages(
                        Set.of(
                                buildStageEntity(workPathId, 0),
                                buildStageEntity(workPathId, 1)
                        )
                ).build();

        WorkPathEntity newWorkPathEntity = WorkPathEntity.builder()
                .name(input.getName())
                .id(workPathId)
                .chapterId(oldWorkPathEntity.getChapterId())
                .stages(oldWorkPathEntity.getStages())
                .build();


        WorkPath expectedResult = WorkPath.builder()
                .setId(newWorkPathEntity.getId())
                .setName(newWorkPathEntity.getName())
                .setChapterId(newWorkPathEntity.getChapterId())
                .setStages(newWorkPathEntity.getStages().stream().map(stageMapper::entityToDto).toList())
                .build();

        //mock database
        when(workPathRepository.existsById(input.getId())).thenReturn(true);
        when(workPathRepository.getReferenceById(input.getId())).thenReturn(oldWorkPathEntity);
        when(workPathRepository.save(any())).thenReturn(newWorkPathEntity);

        // execute method under test
        WorkPath result = workPathService.updateWorkPath(input);

        verify(workPathRepository, times(1)).save(newWorkPathEntity);

        assertEquals(expectedResult.getId(), result.getId());
        assertEquals(expectedResult.getName(), result.getName());
        assertEquals(expectedResult.getChapterId(), result.getChapterId());
        assertEquals(expectedResult.getStages(), result.getStages());
        assertEquals(expectedResult, result);
    }

    @Test
    void deleteWorkPath() {
        UUID input = UUID.randomUUID();

        //mock database
        when(workPathRepository.existsById(input)).thenReturn(true);
        doNothing().when(workPathRepository).deleteById(input);

        UUID result = workPathService.deleteWorkPath(input);

        verify(workPathRepository, times(1)).deleteById(input);
        assertEquals(input, result);
    }

    @Test
    void deleteWorkInvalidIdPath() {
        UUID input = UUID.randomUUID();

        //mock database
        when(workPathRepository.existsById(input)).thenReturn(false);

        assertThrows(EntityNotFoundException.class, () -> workPathService.deleteWorkPath(input));
    }

    // case: valid input provided
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

        List<UUID> sortedStageIds = stageEntities.stream().map(StageEntity::getId).sorted().toList();


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

        for (Stage stage : result.getStages()) {
            assertEquals(sortedStageIds.indexOf(stage.getId()), stage.getPosition());
        }
    }

    // case: received stage ID list contains elements that are not part of the work Path
    @Test
    void reorderStagesInvalidStageListTest() {

        UUID workPathId = UUID.randomUUID();

        List<StageEntity> stageEntities = List.of(
                buildStageEntity(workPathId, 0),
                buildStageEntity(workPathId, 1),
                buildStageEntity(workPathId, 2),
                buildStageEntity(workPathId, 3)
        );

        WorkPathEntity workPathEntity = WorkPathEntity.builder()
                .id(workPathId)
                .name("Work-Path 1")
                .chapterId(UUID.randomUUID())
                .stages(Set.copyOf(stageEntities.subList(0, 2)))
                .build();

        List<UUID> sortedStageIds = stageEntities.stream()
                .map(StageEntity::getId)
                .sorted()
                .toList();


        StageOrderInput stageOrderInput = StageOrderInput.builder()
                .setWorkPathId(workPathId)
                .setStageIds(sortedStageIds)
                .build();

        //mock database
        when(workPathRepository.getReferenceById(stageOrderInput.getWorkPathId())).thenReturn(workPathEntity);
        when(workPathRepository.save(any())).thenReturn(workPathEntity);

        assertThrows(EntityNotFoundException.class, () -> workPathService.reorderStages(stageOrderInput));

    }

    // case: received stage ID list is incomplete
    @Test
    void reorderStagesIncompleteStageListTest() {

        UUID workPathId = UUID.randomUUID();

        List<StageEntity> stageEntities = List.of(
                buildStageEntity(workPathId, 0),
                buildStageEntity(workPathId, 1),
                buildStageEntity(workPathId, 2),
                buildStageEntity(workPathId, 3)
        );

        WorkPathEntity workPathEntity = WorkPathEntity.builder()
                .id(workPathId)
                .name("Work-Path 1")
                .chapterId(UUID.randomUUID())
                .stages(Set.copyOf(stageEntities))
                .build();

        List<UUID> sortedStageIds = stageEntities.subList(0, 2)
                .stream()
                .map(StageEntity::getId)
                .sorted()
                .toList();


        StageOrderInput stageOrderInput = StageOrderInput.builder()
                .setWorkPathId(workPathId)
                .setStageIds(sortedStageIds)
                .build();

        //mock database
        when(workPathRepository.getReferenceById(stageOrderInput.getWorkPathId())).thenReturn(workPathEntity);
        when(workPathRepository.save(any())).thenReturn(workPathEntity);

        assertThrows(EntityNotFoundException.class, () -> workPathService.reorderStages(stageOrderInput));

    }

    @Test
    void getWorkPathByChapterId() {
        UUID chapterId = UUID.randomUUID();
        List<WorkPathEntity> workPathEntities = List.of(
                WorkPathEntity.builder()
                        .id(UUID.randomUUID())
                        .name("Work-Path 1")
                        .chapterId(chapterId)
                        .stages(new HashSet<>())
                        .build(),
                WorkPathEntity.builder()
                        .id(UUID.randomUUID())
                        .name("Work-Path 1")
                        .chapterId(chapterId)
                        .stages(new HashSet<>())
                        .build()
        );

        List<WorkPath> expectedResult = workPathEntities.stream().map(workPathMapper::entityToDto).toList();

        // mock database
        when(workPathRepository.findWorkPathEntitiesByChapterId(chapterId)).thenReturn(workPathEntities);

        List<WorkPath> result = workPathService.getWorkPathByChapterId(chapterId);

        assertEquals(expectedResult, result);
    }

    private StageEntity buildStageEntity(UUID workPathId, int pos) {
        return StageEntity.builder()
                .id(UUID.randomUUID())
                .workPathId(workPathId)
                .position(pos)
                .requiredContents(new HashSet<>())
                .optionalContent(new HashSet<>())
                .build();
    }
}