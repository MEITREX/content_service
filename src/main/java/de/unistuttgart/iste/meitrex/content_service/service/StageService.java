package de.unistuttgart.iste.meitrex.content_service.service;

import de.unistuttgart.iste.meitrex.content_service.persistence.entity.*;
import de.unistuttgart.iste.meitrex.content_service.persistence.mapper.StageMapper;
import de.unistuttgart.iste.meitrex.content_service.persistence.repository.*;
import de.unistuttgart.iste.meitrex.generated.dto.*;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
@RequiredArgsConstructor
public class StageService {

    private final StageRepository stageRepository;
    private final SectionRepository sectionRepository;
    private final ContentRepository contentRepository;
    private final StageMapper stageMapper;

    /**
     * creates a new Stage for an existing Section
     *
     * @param sectionId Section ID the Stage belongs to
     * @return created Stage
     */
    public Stage createNewStage(final UUID sectionId, final CreateStageInput input) {
        final SectionEntity sectionEntity = requireSectionExisting(sectionId);
        final StageEntity stageEntity = StageEntity.builder()
                .sectionId(sectionId)
                .position(sectionEntity.getStages().size())
                .requiredContents(getAndValidateContentsOfStage(
                        sectionEntity.getChapterId(),
                        input.getRequiredContents()))
                .optionalContents(getAndValidateContentsOfStage(
                        sectionEntity.getChapterId(),
                        input.getOptionalContents()))
                .build();

        return stageMapper.entityToDto(stageRepository.save(stageEntity));
    }

    /**
     * Updates the Content of an existing Stage
     *
     * @param input Update Input. Fields must not be null
     * @return updated Stage
     */
    public Stage updateStage(final UpdateStageInput input) {
        final StageEntity stageEntity = requireStageExisting(input.getId());
        final SectionEntity sectionEntity = requireSectionExisting(stageEntity.getSectionId());

        // set updated Content
        stageEntity.setRequiredContents(
                getAndValidateContentsOfStage(
                        sectionEntity.getChapterId(),
                        input.getRequiredContents()
                ));

        stageEntity.setOptionalContents(
                getAndValidateContentsOfStage(
                        sectionEntity.getChapterId(),
                        input.getOptionalContents()
                ));

        return stageMapper.entityToDto(stageRepository.save(stageEntity));
    }

    /**
     * validates that received content is located in the same chapter as the Section / Stage.
     * If Content is not part of the same chapter, the content is removed
     *
     * @param chapterId  chapter ID of the Section / Stage
     * @param contentIds List of Content IDs to be validated
     * @return Set of validated Content Entities
     */
    private Set<ContentEntity> getAndValidateContentsOfStage(final UUID chapterId, final List<UUID> contentIds) {
        final Set<ContentEntity> resultSet = new HashSet<>();

        final List<ContentEntity> contentEntities = contentRepository.findAllById(contentIds);

        for (final ContentEntity contentEntity : contentEntities) {
            // only add content that is located in the same chapter as the Work-Path / Stage
            if (contentEntity.getMetadata().getChapterId().equals(chapterId)) {
                resultSet.add(contentEntity);
            }
        }

        return resultSet;
    }

    /**
     * Deletes a Stage via ID
     *
     * @param stageId Stage ID to be removed
     * @return ID of deleted Stage
     */
    public UUID deleteStage(final UUID stageId) {
        final StageEntity deletedStageEntity = requireStageExisting(stageId);

        final SectionEntity sectionEntity = sectionRepository.getReferenceById(deletedStageEntity.getSectionId());

        sectionEntity.getStages().remove(deletedStageEntity);

        //if a stage is deleted all subsequent stages have to have their position moved up by 1 in the list
        for (final StageEntity entity : sectionEntity.getStages()) {

            if (entity.getPosition() > deletedStageEntity.getPosition()) {
                //move entity one position up
                entity.setPosition(entity.getPosition() - 1);
            }
        }
        // perform deletion
        stageRepository.delete(deletedStageEntity);
        sectionRepository.save(sectionEntity);

        return deletedStageEntity.getId();
    }

    /**
     * Helper function to deleted Content Links in Stage Entities if Content gets deleted
     *
     * @param contentEntity a content Entity that is up for deletion
     */
    public void deleteContentLinksFromStages(final ContentEntity contentEntity) {
        final List<StageEntity> stageEntities = stageRepository.findAllByRequiredContentsContainingOrOptionalContentsContaining(contentEntity, contentEntity);

        for (final StageEntity stageEntity : stageEntities) {
            stageEntity.getRequiredContents().remove(contentEntity);
            stageEntity.getOptionalContents().remove(contentEntity);
        }
        stageRepository.saveAll(stageEntities);
    }

    /**
     * Finds the stage the content with the specified ID is part of. Returns empty optional if content is not part
     * of any stage or content with specified ID does not exist.
     * @param contentId The ID of the content.
     * @return Returns an Optional containing the Stage the content belongs to, or an empty optional if the content
     * does not belong to any stage or if no content with the given ID exists.
     */
    public Optional<Stage> findStageOfContent(UUID contentId) {
        Optional<ContentEntity> content = contentRepository.findById(contentId);

        if(content.isEmpty())
            return Optional.empty();

        List<StageEntity> stages = stageRepository
                .findAllByRequiredContentsContainingOrOptionalContentsContaining(content.get(), content.get());

        if(stages.isEmpty())
            return Optional.empty();

        // content can only be part of one stage max
        if(stages.size() > 1)
            throw new RuntimeException("Content is part of more than one stage. This should not be possible!");

        return Optional.of(stageMapper.entityToDto(stages.getFirst()));
    }

    /**
     * Checks if a Stage exists.
     *
     * @param uuid The id of the Stage to check.
     * @return The StageEntity with the given id.
     * @throws EntityNotFoundException If the chapter does not exist.
     */
    private StageEntity requireStageExisting(final UUID uuid) {
        return stageRepository.findById(uuid)
                .orElseThrow(() -> new EntityNotFoundException("Stage with id " + uuid + " not found"));
    }

    /**
     * Checks if a Section exists.
     *
     * @param uuid The id of the Section to check.
     * @return The SectionEntity with the given id.
     * @throws EntityNotFoundException If the chapter does not exist.
     */
    private SectionEntity requireSectionExisting(final UUID uuid) {
        return sectionRepository.findById(uuid)
                .orElseThrow(() -> new EntityNotFoundException("Section with id " + uuid + " not found"));
    }

}
