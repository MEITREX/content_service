package de.unistuttgart.iste.gits.content_service.service;

import de.unistuttgart.iste.gits.generated.dto.CreateTagInputDto;
import de.unistuttgart.iste.gits.generated.dto.TagDto;
import de.unistuttgart.iste.gits.generated.dto.UpdateTagInputDto;
import de.unistuttgart.iste.gits.content_service.persistence.dao.TagEntity;
import de.unistuttgart.iste.gits.content_service.persistence.mapper.TagMapper;
import de.unistuttgart.iste.gits.content_service.persistence.repository.TagRepository;
import de.unistuttgart.iste.gits.content_service.validation.TagValidator;
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
