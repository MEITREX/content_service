package de.unistuttgart.iste.gits.content.service;

import de.unistuttgart.iste.gits.content.dto.CreateTagInputDto;
import de.unistuttgart.iste.gits.content.dto.TagDto;
import de.unistuttgart.iste.gits.content.dto.UpdateTagInputDto;
import de.unistuttgart.iste.gits.content.persistence.dao.ContentEntity;
import de.unistuttgart.iste.gits.content.persistence.dao.TagEntity;
import de.unistuttgart.iste.gits.content.persistence.mapper.TagMapper;
import de.unistuttgart.iste.gits.content.persistence.repository.TagRepository;
import de.unistuttgart.iste.gits.content.validation.TagValidator;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;
@Service
@RequiredArgsConstructor
public class TagService {

    private final TagRepository tagRepository;
    private final TagMapper tagMapper;
    private final TagValidator tagValidator;

    public List<TagDto> getAllTags() {
        return tagRepository.findAll().stream().map(tagMapper::entityToDto).toList();
    }

    public TagDto createTag(CreateTagInputDto tagInputDto) {
        tagValidator.validateCreateTagInputDto(tagInputDto);
        TagEntity tagEntity = tagMapper.dtoToEntity(tagInputDto);
        tagEntity = tagRepository.save(tagEntity);
        return tagMapper.entityToDto(tagEntity);
    }

    public TagDto updateTag(UpdateTagInputDto input) {
        tagValidator.validateUpdateTagInputDto(input);
        requireTagExisting(input.getId());

        TagEntity updatedTagEntity = tagRepository.save(tagMapper.dtoToEntity(input));

        return tagMapper.entityToDto(updatedTagEntity);
    }

    public UUID deleteTag(UUID uuid) {
        requireTagExisting(uuid);
        tagRepository.deleteById(uuid);
        return uuid;
    }

    public void requireTagExisting(UUID id) {
        if (!tagRepository.existsById(id)) {
            throw new EntityNotFoundException("Tag with id " + id + " not found");
        }
    }
}
