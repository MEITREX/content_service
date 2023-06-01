package de.unistuttgart.iste.gits.content_service.service;

import de.unistuttgart.iste.gits.content_service.persistence.dao.TagEntity;
import de.unistuttgart.iste.gits.content_service.persistence.repository.TagRepository;
import de.unistuttgart.iste.gits.generated.dto.ContentDto;
import de.unistuttgart.iste.gits.generated.dto.CreateContentInputDto;
import de.unistuttgart.iste.gits.generated.dto.UpdateContentInputDto;
import de.unistuttgart.iste.gits.content_service.persistence.dao.ContentEntity;
import de.unistuttgart.iste.gits.content_service.persistence.mapper.ContentMapper;
import de.unistuttgart.iste.gits.content_service.persistence.repository.ContentRepository;
import de.unistuttgart.iste.gits.content_service.validation.ContentValidator;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class ContentService {

    private final ContentRepository contentRepository;
    private final TagRepository tagRepository;
    private final ContentMapper contentMapper;
    private final ContentValidator contentValidator;

    public static class TagSynchronizationResult {
        public final Collection<TagEntity> existingTagsToAdd;
        public final Collection<TagEntity> newTagsToAdd;
        public final Collection<TagEntity> existingTagsToRemove;

        public TagSynchronizationResult(Collection<TagEntity> existingTagsToAdd,
                                        Collection<TagEntity> newTagsToAdd,
                                        Collection<TagEntity> existingTagsToRemove) {
            this.existingTagsToAdd = existingTagsToAdd;
            this.newTagsToAdd = newTagsToAdd;
            this.existingTagsToRemove = existingTagsToRemove;
        }
    }

    TagSynchronizationResult prepareSynchronization(List<String> tagNames,
                                                    List<TagEntity> currentTags,
                                                    List<TagEntity> existingTagsWithGivenNames) {
        Set<String> tagNamesSet = new HashSet<>(tagNames);
        Set<String> currentTagNames = currentTags.stream().map(tag -> tag.getName()).collect(Collectors.toSet());
        Set<String> tagNamesToAdd = tagNamesSet.stream().filter(name -> !currentTagNames.contains(name)).collect(Collectors.toSet());
        List<TagEntity> existingTagsToAdd = existingTagsWithGivenNames.stream().filter(tag -> tagNamesToAdd.contains(tag.getName())).collect(Collectors.toList());
        Set<String> existingTagNames = existingTagsToAdd.stream().map(tag -> tag.getName()).collect(Collectors.toSet());
        Set<String> tagNamesToCreate = tagNamesToAdd.stream().filter(name -> !existingTagNames.contains(name)).collect(Collectors.toSet());
        List<TagEntity> newTags = tagNamesToCreate.stream().map(TagEntity::fromName).collect(Collectors.toList());
        Set<String> tagNamesToRemove = currentTagNames.stream().filter(name -> !tagNamesSet.contains(name)).collect(Collectors.toSet());
        List<TagEntity> tagsToRemove = currentTags.stream().filter(tag -> tagNamesToRemove.contains(tag.getName())).collect(Collectors.toList());
        return new TagSynchronizationResult(existingTagsToAdd,newTags,tagsToRemove);
    }

    void synchronizeWithDatabase(ContentEntity content, TagSynchronizationResult tagSynchronizationResult) {
        // the prepareSynchronization created TagEntity instances for new tags but did not store them --> save them to the DB
        Set<TagEntity> tagsToAdd = tagSynchronizationResult.newTagsToAdd.stream().map(tag -> tagRepository.save(tag)).collect(Collectors.toSet());
        // the newly created tags and the existing tags which are not already assigned to the content need to be linked with the content
        // for n:m relationships the links have to be created on both sides, i.e. content -> tag and tag --> content
        tagsToAdd.addAll(tagSynchronizationResult.existingTagsToAdd);
        if (content.getTags() == null) {
            content.setTags(tagsToAdd); // TODO clarify if this can really happen or if an empty list is returned
        } else {
            content.getTags().addAll(tagsToAdd);
        }
        for (TagEntity tag : tagsToAdd) {
            if (tag.getContents()==null) {
                tag.setContents(new HashSet<>(Set.of(content))); // TODO clarify if this can really happen or if an empty list is returned
            } else {
                tag.getContents().add(content);
            }
        }
        // Remove links between content and tag
        // Note: The tag entity is not deleted from the DB even it is not referenced by any content
        for (TagEntity tagEntity : tagSynchronizationResult.existingTagsToRemove) {
            content.removeFromTags(tagEntity);
            tagEntity.removeFromContents(content);
        }
    }

    protected void synchronizeTags(ContentEntity content, List<String> tagNames){
        List<TagEntity> currentTags = tagRepository.findByContentId(content.getId());
        List<TagEntity> existingTagsWithGivenNames = tagRepository.findByNames(tagNames);
        TagSynchronizationResult result = prepareSynchronization(tagNames, currentTags, existingTagsWithGivenNames);
        synchronizeWithDatabase(content,result);
    }

    public List<ContentDto> getAllContents() {
        return contentRepository.findAll().stream().map(contentMapper::entityToDto).toList();
    }

    public ContentDto createContent(CreateContentInputDto contentInputDto) {
        contentValidator.validateCreateContentInputDto(contentInputDto);
        ContentEntity contentEntity = contentMapper.dtoToEntity(contentInputDto);
        contentEntity = contentRepository.save(contentEntity);
        synchronizeTags(contentEntity,contentInputDto.getTagNames());
        return contentMapper.entityToDto(contentEntity);
    }

    public ContentDto updateContent(UpdateContentInputDto input) {
        contentValidator.validateUpdateContentInputDto(input);
        requireContentExisting(input.getId());

        ContentEntity updatedContentEntity = contentRepository.save(contentMapper.dtoToEntity(input));
        synchronizeTags(updatedContentEntity, input.getTagNames());
        return contentMapper.entityToDto(updatedContentEntity);
    }

    public UUID deleteContent(UUID uuid) {
        requireContentExisting(uuid);
        contentRepository.deleteById(uuid);
        return uuid;
    }

    /**
     * Checks if a Content with the given id exists. If not, an EntityNotFoundException is thrown.
     * @param id The id of the Content to check.
     * @throws EntityNotFoundException If a Content with the given id does not exist.
     */
    public void requireContentExisting(UUID id) {
        if (!contentRepository.existsById(id)) {
            throw new EntityNotFoundException("Content with id " + id + " not found");
        }
    }

    public List<ContentDto> getContentsById(List<UUID> ids) {
        return contentRepository.findById(ids).stream().map(contentMapper::entityToDto).toList();
    }

    public List<List<ContentDto>> getContentsByChapterIds(List<UUID> chapterIds) {
        List<List<ContentDto>> result = new ArrayList<>(chapterIds.size());

        // get a list containing all contents with a matching chapter id, then map them by chapter id (multiple
        // contents might have the same chapter id)
        Map<UUID, List<ContentDto>> contentsByChapterId = contentRepository.findByChapterIds(chapterIds).stream()
                .map(contentMapper::entityToDto)
                .collect(Collectors.groupingBy(ContentDto::getChapterId));

        // put the different groups of chapters into the result list such that the order matches the order
        // of chapter ids given by the chapterIds argument
        for(UUID chapterId : chapterIds) {
            List<ContentDto> contents = contentsByChapterId.getOrDefault(chapterId, Collections.emptyList());

            result.add(contents);
        }

        return result;
    }

    public ContentDto addTagToContent(UUID id, String tagName) {
        requireContentExisting(id);
        ContentEntity content = contentRepository.getReferenceById(id);
        // The repository should return at most one tag as one content should not have multiple tags with the same name
        List<TagEntity> currentTagsWithThisName = tagRepository.findByContentIdAndTagName(content.getId(),tagName);
        if (currentTagsWithThisName.size() == 0) {
            List<TagEntity> existingTags = tagRepository.findByName(tagName);
            TagEntity tagEntity = null;
            if (existingTags.size() == 0) {
                // There is no tag with this name
                tagEntity = TagEntity.fromName(tagName);
                tagRepository.save(tagEntity);
            } else {
                tagEntity = existingTags.get(0);
            }
            content.addToTags(tagEntity);
            tagEntity.addToContents(content);
        }
        return contentMapper.entityToDto(content);
    }

    public ContentDto removeTagFromContent(UUID id, String tagName) {
        requireContentExisting(id);
        ContentEntity content = contentRepository.getReferenceById(id);
        // The repository should return at most one tag as one content should not have multiple tags with the same name
        List<TagEntity> currentTagsWithThisName = tagRepository.findByContentIdAndTagName(content.getId(),tagName);
        for (TagEntity tagEntity : currentTagsWithThisName) {
            content.removeFromTags(tagEntity);
            tagEntity.removeFromContents(content);
        }
        return contentMapper.entityToDto(content);
    }

}
