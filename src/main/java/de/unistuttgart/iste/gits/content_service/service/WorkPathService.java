package de.unistuttgart.iste.gits.content_service.service;

import de.unistuttgart.iste.gits.content_service.persistence.dao.StageEntity;
import de.unistuttgart.iste.gits.content_service.persistence.dao.WorkPathEntity;
import de.unistuttgart.iste.gits.content_service.persistence.mapper.WorkPathMapper;
import de.unistuttgart.iste.gits.content_service.persistence.repository.WorkPathRepository;
import de.unistuttgart.iste.gits.generated.dto.*;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class WorkPathService {

    private final WorkPathMapper workPathMapper;
    private final WorkPathRepository workPathRepository;

    /**
     * creates a new Work-Path for a given chapterId and name
     * @param input input object containing a chapter ID and name
     * @return new Work Path Object
     */
    public WorkPath createWorkPath(CreateWorkPathInput input){
        WorkPathEntity workPathEntity = workPathRepository.save(
                WorkPathEntity.builder()
                        .name(input.getName())
                        .chapterId(input.getChapterId())
                        .stages(new HashSet<>())
                        .build()
        );
        return workPathMapper.entityToDto(workPathEntity);
    }

    /**
     * Updates the name of a Work Path
     * @param input input object containing id and new name
     * @return updated Work-Path object
     */
    public WorkPath updateWorkPath(UpdateWorkPathInput input){

        requireWorkPathExisting(input.getId());
        //updates name only!
        WorkPathEntity workPathEntity = workPathRepository.getReferenceById(input.getId());
        workPathEntity.setName(input.getName());
        workPathEntity = workPathRepository.save(workPathEntity);
        return workPathMapper.entityToDto(workPathEntity);
    }

    /**
     * deletes a Work-Path via ID
     * @param workPathId ID of Work-Path
     * @return ID of deleted Object
     */
    public UUID deleteWorkPath(UUID workPathId){
        requireWorkPathExisting(workPathId);

        workPathRepository.deleteById(workPathId);

        return workPathId;
    }

    /**
     * changes the order of Stages within a Work-Path
     * @param input order list of stage IDs describing new Stage Order
     * @return updated Work Path with new Stage Order
     */
    public WorkPath reorderStages(StageOrderInput input){

        WorkPathEntity workPathEntity = workPathRepository.getReferenceById(input.getWorkPathId());

        //ensure received list is complete
        validateStageIds(input.getStageIds(), workPathEntity.getStages());

        for (StageEntity stageEntity: workPathEntity.getStages()) {

            int newPos = input.getStageIds().indexOf(stageEntity.getId());

            stageEntity.setPosition(newPos);
        }

        // persist changes
        workPathRepository.save(workPathEntity);

        return workPathMapper.entityToDto(workPathEntity);
    }

    /**
     * ensures received ID list is complete
     * @param receivedStageIds received ID list
     * @param stageEntities found entities in database
     */
    private void validateStageIds(List<UUID> receivedStageIds, Set<StageEntity> stageEntities){
        if (receivedStageIds.size() > stageEntities.size()){
            throw new EntityNotFoundException("Stage ID list contains more elements than expected");
        }
        List<UUID> stageIds = stageEntities.stream().map(StageEntity::getId).toList();
        for (UUID stageId: stageIds) {
            if (!receivedStageIds.contains(stageId)){
                throw new EntityNotFoundException("Incomplete Stage ID list received");
            }
        }
    }

    /**
     * find all Work-Paths for a Chapter ID
     * @param uuid chapter ID
     * @return all Work-Paths that have received chapter ID in form of a List
     */
    public List<WorkPath> getWorkPathByChapterId(UUID uuid){
        List<WorkPathEntity> entities = workPathRepository.findWorkPathEntitiesByChapterId(uuid);

        return entities.stream()
                .map(workPathMapper::entityToDto)
                .toList();
    }

    /**
     * Checks if a Work-Path exists.
     *
     * @param uuid The id of the Work-Path to check.
     * @throws EntityNotFoundException If the chapter does not exist.
     */
    private void requireWorkPathExisting(UUID uuid) {
        if (!workPathRepository.existsById(uuid)) {
            throw new EntityNotFoundException("Work-Path with id " + uuid + " not found");
        }
    }

}
