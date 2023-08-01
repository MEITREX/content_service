package de.unistuttgart.iste.gits.content_service.service;

import de.unistuttgart.iste.gits.content_service.persistence.dao.ContentEntity;
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

import java.util.*;

@Service
@RequiredArgsConstructor
public class StageService {

    private final StageRepository stageRepository;
    private final WorkPathRepository workPathRepository;
    private final ContentRepository contentRepository;
    private final StageMapper stageMapper;

    /**
     * creates a new Stage for an existing Work-Path
     * @param workPathId Work Path ID the Stage belongs to
     * @return created Stage
     */
    public Stage createNewStage(UUID workPathId){

        requireWorkPathExisting(workPathId);

        WorkPathEntity workPathEntity = workPathRepository.getReferenceById(workPathId);
        StageEntity stageEntity = StageEntity.builder()
                .workPathId(workPathId)
                .position(workPathEntity.getStages().size())
                .requiredContents(new HashSet<>())
                .optionalContent(new HashSet<>())
                .build();

        return stageMapper.entityToDto(stageRepository.save(stageEntity));
    }

    /**
     * Updates the Content of an existing Stage
     * @param input Update Input. Fields must not be null
     * @return updated Stage
     */
    public Stage updateStage(UpdateStageInput input){

        if (input.getId() == null || input.getRequiredContents() == null || input.getOptionalContents() == null ){
            throw new NullPointerException("request fields must not be null");
        }

        requireStageExisting(input.getId());

        // fetch old Stage Object
        StageEntity stageEntity = stageRepository.getReferenceById(input.getId());

        requireWorkPathExisting(stageEntity.getWorkPathId());

        // fetch Work Path, Stage belongs to
        WorkPathEntity workPathEntity = workPathRepository.getReferenceById(stageEntity.getWorkPathId());

        // set updated Content
        stageEntity.setRequiredContents(
                validateStageContent(
                        workPathEntity.getChapterId(),
                        input.getRequiredContents()
                ));

        stageEntity.setOptionalContent(
                validateStageContent(
                        workPathEntity.getChapterId(),
                        input.getOptionalContents()
                ));

        return stageMapper.entityToDto(stageRepository.save(stageEntity));
    }

    /**
     * validates that received content is located in the same chapter as the Work-Path / Stage.
     * If Content is not part of the same chapter, the content is removed
     * @param chapterId chapter ID of the Work-Path / Stage
     * @param contentIds
     * @return
     */
    private Set<ContentEntity> validateStageContent(UUID chapterId, List<UUID> contentIds) {

        Set<ContentEntity> resultSet = new HashSet<>();

        List<ContentEntity> contentEntities = contentRepository.findContentEntitiesByIdIn(contentIds);

        for (ContentEntity contentEntity: contentEntities) {
            // only add content that is located in the same chapter as the Work-Path / Stage
            if (contentEntity.getMetadata().getChapterId().equals(chapterId)){
                resultSet.add(contentEntity);
            }
        }

        return resultSet;
    }

    /**
     * Deletes a Stage via ID
     * @param stageId Stage ID to be removed
     * @return ID of deleted Stage
     */
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
        // perform deletion
        stageRepository.delete(deletedStageEntity);

        return deletedStageEntity.getId();
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
        if (uuid == null){
            throw new NullPointerException("Work-Path ID must be not nulL!");
        }
        if (!workPathRepository.existsById(uuid)) {
            throw new EntityNotFoundException("Work-Path with id " + uuid + " not found");
        }
    }


}
