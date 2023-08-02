package de.unistuttgart.iste.gits.content_service.service;

import de.unistuttgart.iste.gits.content_service.persistence.dao.ContentEntity;
import de.unistuttgart.iste.gits.content_service.persistence.dao.ContentMetadataEmbeddable;
import de.unistuttgart.iste.gits.content_service.persistence.dao.StageEntity;
import de.unistuttgart.iste.gits.content_service.persistence.dao.WorkPathEntity;
import de.unistuttgart.iste.gits.content_service.persistence.mapper.ContentMapper;
import de.unistuttgart.iste.gits.content_service.persistence.mapper.StageMapper;
import de.unistuttgart.iste.gits.content_service.persistence.repository.ContentRepository;
import de.unistuttgart.iste.gits.content_service.persistence.repository.StageRepository;
import de.unistuttgart.iste.gits.content_service.persistence.repository.WorkPathRepository;
import de.unistuttgart.iste.gits.generated.dto.*;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.modelmapper.ModelMapper;

import java.time.OffsetDateTime;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class StageServiceTest {

    private final StageRepository stageRepository = Mockito.mock(StageRepository.class);
    private final WorkPathRepository workPathRepository = Mockito.mock(WorkPathRepository.class);

    private final ContentRepository contentRepository = Mockito.mock(ContentRepository.class);

    private final StageMapper stageMapper = new StageMapper(new ContentMapper(new ModelMapper()));

    private final StageService stageService = new StageService(
            stageRepository,
            workPathRepository,
            contentRepository,
            stageMapper);

    @Test
    void createNewStageTest() {
        WorkPathEntity workPathEntity = WorkPathEntity.builder().id(UUID.randomUUID()).name("Test Work-Path").stages(new HashSet<>()).chapterId(UUID.randomUUID()).build();

        StageEntity stageEntity = StageEntity.builder().workPathId(workPathEntity.getId()).position(0).optionalContent(new HashSet<>()).requiredContents(new HashSet<>()).build();

        //mock repository
        when(workPathRepository.existsById(workPathEntity.getId())).thenReturn(true);
        when(workPathRepository.getReferenceById(workPathEntity.getId())).thenReturn(workPathEntity);
        when(stageRepository.save(any())).thenReturn(stageEntity);


        //execute method under test
        Stage result = stageService.createNewStage(workPathEntity.getId());

        assertEquals(0, result.getPosition());
        assertTrue(result.getRequiredContents().isEmpty());
        assertTrue(result.getOptionalContents().isEmpty());

    }

    @Test
    void createNewStageWithInvalidWorkPathId(){
        UUID id = UUID.randomUUID();

        WorkPathEntity workPathEntity = WorkPathEntity.builder().id(id).name("Test Work-Path").stages(new HashSet<>()).chapterId(UUID.randomUUID()).build();

        when(workPathRepository.existsById(workPathEntity.getId())).thenReturn(false);

        assertThrows(EntityNotFoundException.class, () -> stageService.createNewStage(id));
        assertThrows(NullPointerException.class, () -> stageService.createNewStage(null));
    }

    @Test
    void updateStageTest() {
        //init data
        UUID chapterId = UUID.randomUUID();
        UUID workPathId = UUID.randomUUID();
        UUID stageId = UUID.randomUUID();

        List<ContentEntity> expectedReqContents = List.of(
                buildContentEntity(chapterId) ,
                buildContentEntity(chapterId));
        List<ContentEntity> expectedOptContents = List.of(
                buildContentEntity(chapterId),
                buildContentEntity(chapterId)
        );

        StageEntity oldStageEntity = StageEntity.builder()
                .id(stageId)
                .position(0)
                .workPathId(workPathId)
                .requiredContents(Set.of(expectedReqContents.get(0)))
                .optionalContent(new HashSet<>())
                .build();

        StageEntity expectedStageEntity = StageEntity.builder()
                .id(stageId)
                .position(0)
                .workPathId(workPathId)
                .requiredContents(Set.copyOf(expectedReqContents))
                .optionalContent(Set.copyOf(expectedOptContents))
                .build();

        Stage expectedResult = stageMapper.entityToDto(expectedStageEntity);

        WorkPathEntity workPathEntity = WorkPathEntity.builder()
                .id(workPathId)
                .name("Test1")
                .chapterId(chapterId)
                .stages(Set.of(oldStageEntity))
                .build();

        UpdateStageInput input = UpdateStageInput.builder()
                .setId(stageId)
                .setRequiredContents(
                        expectedReqContents.stream()
                                .map( content -> content.getId())
                                .toList()
                ).setOptionalContents(
                        expectedOptContents.stream()
                                .map( content -> content.getId())
                                .toList()
                ).build();

        //mock database
        when(stageRepository.existsById(any())).thenReturn(true);
        when(stageRepository.getReferenceById(input.getId())).thenReturn(oldStageEntity);
        when(workPathRepository.existsById(any())).thenReturn(true);
        when(workPathRepository.getReferenceById(oldStageEntity.getWorkPathId())).thenReturn(workPathEntity);
        when(contentRepository.findContentEntitiesByIdIn(input.getRequiredContents())).thenReturn(expectedReqContents);
        when(contentRepository.findContentEntitiesByIdIn(input.getOptionalContents())).thenReturn(expectedOptContents);
        when(stageRepository.save(any())).thenReturn(oldStageEntity);

        //execute method under test
        Stage result = stageService.updateStage(input);


        assertEquals(expectedStageEntity.getId(), result.getId());
        assertEquals(expectedStageEntity.getPosition(), result.getPosition());

        assertEquals(2, result.getRequiredContents().size());
        assertEquals(2, result.getOptionalContents().size());

        assertEquals(expectedResult, result);

    }

    @Test
    void updateStageMissingStageTest(){
        //invalid Stage ID
        UpdateStageInput input = UpdateStageInput.builder()
                .setId(UUID.randomUUID())
                .setRequiredContents(new ArrayList<>())
                .setOptionalContents(new ArrayList<>())
                .build();

        //mock database
        when(stageRepository.existsById(any())).thenReturn(false);

        //execute method under test
        assertThrows(EntityNotFoundException.class, () -> stageService.updateStage(input));

    }

    @Test
    void updateStageInvalidWorkPathTest(){
        //invalid Work-Path ID
        UpdateStageInput input = UpdateStageInput.builder()
                .setId(UUID.randomUUID())
                .setRequiredContents(new ArrayList<>())
                .setOptionalContents(new ArrayList<>())
                .build();
        StageEntity oldStageEntity = StageEntity.builder()
                .id(UUID.randomUUID())
                .position(0)
                .workPathId(UUID.randomUUID())
                .requiredContents(new HashSet<>())
                .optionalContent(new HashSet<>())
                .build();

        //mock database
        when(stageRepository.existsById(any())).thenReturn(true);
        when(stageRepository.getReferenceById(input.getId())).thenReturn(oldStageEntity);
        when(workPathRepository.existsById(any())).thenReturn(false);

        //execute method under test
        assertThrows(EntityNotFoundException.class, () -> stageService.updateStage(input));
    }

    @Test
    void updateStageMixedChapterIds(){
        // content with wrong chapter ID
        //init data
        UUID chapterId = UUID.randomUUID();
        UUID workPathId = UUID.randomUUID();
        UUID stageId = UUID.randomUUID();

        List<ContentEntity> expectedReqContents = List.of(
                buildContentEntity(chapterId) ,
                buildContentEntity(chapterId));
        List<ContentEntity> expectedOptContents = List.of(
                buildContentEntity(chapterId),
                buildContentEntity(UUID.randomUUID())
        );

        StageEntity oldStageEntity = StageEntity.builder()
                .id(stageId)
                .position(0)
                .workPathId(workPathId)
                .requiredContents(Set.of(expectedReqContents.get(0)))
                .optionalContent(new HashSet<>())
                .build();

        StageEntity expectedStageEntity = StageEntity.builder()
                .id(stageId)
                .position(0)
                .workPathId(workPathId)
                .requiredContents(Set.copyOf(expectedReqContents))
                .optionalContent(Set.of(expectedOptContents.get(0)))
                .build();

        Stage expectedResult = stageMapper.entityToDto(expectedStageEntity);

        WorkPathEntity workPathEntity = WorkPathEntity.builder()
                .id(workPathId)
                .name("Test1")
                .chapterId(chapterId)
                .stages(Set.of(oldStageEntity))
                .build();

        UpdateStageInput input = UpdateStageInput.builder()
                .setId(stageId)
                .setRequiredContents(
                        expectedReqContents.stream()
                                .map( content -> content.getId())
                                .toList()
                ).setOptionalContents(
                        expectedOptContents.stream()
                                .map( content -> content.getId())
                                .toList()
                ).build();

        //mock database
        when(stageRepository.existsById(any())).thenReturn(true);
        when(stageRepository.getReferenceById(input.getId())).thenReturn(oldStageEntity);
        when(workPathRepository.existsById(any())).thenReturn(true);
        when(workPathRepository.getReferenceById(oldStageEntity.getWorkPathId())).thenReturn(workPathEntity);
        when(contentRepository.findContentEntitiesByIdIn(input.getRequiredContents())).thenReturn(expectedReqContents);
        when(contentRepository.findContentEntitiesByIdIn(input.getOptionalContents())).thenReturn(expectedOptContents);
        when(stageRepository.save(any())).thenReturn(oldStageEntity);

        //execute method under test
        Stage result = stageService.updateStage(input);

        assertEquals(expectedStageEntity.getId(), result.getId());
        assertEquals(expectedStageEntity.getPosition(), result.getPosition());

        assertEquals(2, result.getRequiredContents().size());
        assertEquals(1, result.getOptionalContents().size());

        assertEquals(expectedResult, result);
    }

    @Test
    void deleteStageTest() {
        //init
        UUID workPathId = UUID.randomUUID();
        StageEntity deletedEntity = buildStageEntity(workPathId, 1);

        Set<StageEntity> stageEntities = Set.of(
                buildStageEntity(workPathId, 0),
                deletedEntity,
                buildStageEntity(workPathId, 2),
                buildStageEntity(workPathId, 3)
        );



        WorkPathEntity workPathEntity = WorkPathEntity.builder()
                .id(workPathId)
                .name("Test123")
                .chapterId(UUID.randomUUID())
                .stages(stageEntities)
                .build();

        //mock database
        when(stageRepository.existsById(any())).thenReturn(true);
        when(stageRepository.getReferenceById(deletedEntity.getId())).thenReturn(deletedEntity);
        when(workPathRepository.getReferenceById(workPathId)).thenReturn(workPathEntity);
        doNothing().when(stageRepository).delete(deletedEntity);

        UUID result = stageService.deleteStage(deletedEntity.getId());

        verify(stageRepository, times(2)).save(any());
        verify(stageRepository, times(1)).delete(deletedEntity);

        assertEquals(deletedEntity.getId(), result);
    }

    @Test
    void deleteStageInvalidIdTest(){

        //mock database
        when(stageRepository.existsById(any())).thenReturn(false);

        UUID uuid = UUID.randomUUID();

        assertThrows(EntityNotFoundException.class, () -> stageService.deleteStage(uuid));
    }

    private ContentEntity buildContentEntity(UUID chapterId){
        return ContentEntity.builder()
                .id(UUID.randomUUID())
                .metadata(
                        ContentMetadataEmbeddable.builder()
                                .tags(new HashSet<>())
                                .type(ContentType.MEDIA)
                                .suggestedDate(OffsetDateTime.now())
                                .rewardPoints(20)
                                .chapterId(chapterId)
                                .build()
                ).build();
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