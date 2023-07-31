package de.unistuttgart.iste.gits.content_service.service;

import de.unistuttgart.iste.gits.content_service.persistence.dao.StageEntity;
import de.unistuttgart.iste.gits.content_service.persistence.dao.WorkPathEntity;
import de.unistuttgart.iste.gits.content_service.persistence.mapper.StageMapper;
import de.unistuttgart.iste.gits.content_service.persistence.repository.ContentRepository;
import de.unistuttgart.iste.gits.content_service.persistence.repository.StageRepository;
import de.unistuttgart.iste.gits.content_service.persistence.repository.WorkPathRepository;
import de.unistuttgart.iste.gits.generated.dto.*;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class StageService {

    StageRepository stageRepository;
    WorkPathRepository workPathRepository;

    ContentRepository contentRepository;

    StageMapper stageMapper;

    public Stage createNewStage(CreateStageInput input){

        requireWorkPathExisting(input.getWorkPathId());

        WorkPathEntity workPathEntity = workPathRepository.getReferenceById(input.getWorkPathId());
        StageEntity stageEntity = stageMapper.dtoToEntity(input);
        stageEntity.setPosition(workPathEntity.getStages().size());
        return stageMapper.entityToDto(stageRepository.save(stageEntity));
    }

    public Stage updateStage(UpdateStageInput input){

        StageEntity stageEntity = stageRepository.getReferenceById(input.getId());

        stageEntity.setRequiredContents(
                Set.copyOf(
                        contentRepository.findContentEntitiesByIdIsIn(input.getRequiredContents()
                        )
                ));
        stageEntity.setOptionalContent(
                Set.copyOf(
                        contentRepository.findContentEntitiesByIdIsIn(input.getOptionalContents()
                        )
                ));

        return stageMapper.entityToDto(stageRepository.save(stageEntity));
    }

    public UUID deleteStage(UUID stageId){

        requireStageExisting(stageId);

        StageEntity deletedStageEntity = stageRepository.getReferenceById(stageId);
        WorkPathEntity workPathEntity = workPathRepository.getReferenceById(deletedStageEntity.getWorkPathId());

        //if a stage is deleted all subsequent stages have to have their position moved up by 1 in the list
        for (StageEntity entity: workPathEntity.getStages()) {
            if (entity.getPosition() > deletedStageEntity.getPosition()){
                //move entity one position up
                entity.setPosition(entity.getPosition() - 1);
                stageRepository.save(entity);
            }
        }

        stageRepository.delete(deletedStageEntity);

        return deletedStageEntity.getId();
    }

    public StageOrder reorderStages(StageOrderInput input){

        List<UUID> stageIds = new ArrayList<>();

        WorkPathEntity workPathEntity = workPathRepository.getReferenceById(input.getWorkPathId());

        for (StageEntity stageEntity: workPathEntity.getStages()) {

            stageEntity.setPosition(input.getStageIds().indexOf(stageEntity.getId()));
            stageIds.add(stageEntity.getId());
        }

        // persist changes
        workPathRepository.save(workPathEntity);

        return StageOrder.builder()
                .setStageIds(stageIds)
                .setWorkPathId(workPathEntity.getId())
                .build();
    }

    /**
     * Checks if a Stage exists.
     *
     * @param uuid The id of the Stage to check.
     * @throws EntityNotFoundException If the chapter does not exist.
     */
    private void requireStageExisting(UUID uuid) {
        if (!stageRepository.existsById(uuid)) {
            throw new EntityNotFoundException("Stage with id " + uuid + " not found");
        }
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
