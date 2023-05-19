package de.unistuttgart.iste.gits.content.service;

import de.unistuttgart.iste.gits.content.dto.ContentDto;
import de.unistuttgart.iste.gits.content.dto.CreateContentInputDto;
import de.unistuttgart.iste.gits.content.dto.UpdateContentInputDto;
import de.unistuttgart.iste.gits.content.persistence.dao.ContentEntity;
import de.unistuttgart.iste.gits.content.persistence.mapper.ContentMapper;
import de.unistuttgart.iste.gits.content.persistence.repository.ContentRepository;
import de.unistuttgart.iste.gits.content.validation.ContentValidator;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional
public class ContentService {

    private final ContentRepository contentRepository;
    private final ContentMapper contentMapper;
    private final ContentValidator contentValidator;
    

    public List<ContentDto> getAllContents() {
        return contentRepository.findAll().stream().map(contentMapper::entityToDto).toList();
    }

    public ContentDto createContent(CreateContentInputDto contentInputDto) {
        contentValidator.validateCreateContentInputDto(contentInputDto);
        ContentEntity contentEntity = contentMapper.dtoToEntity(contentInputDto);
        contentEntity = contentRepository.save(contentEntity);
        return contentMapper.entityToDto(contentEntity);
    }

    public ContentDto updateContent(UpdateContentInputDto input) {
        contentValidator.validateUpdateContentInputDto(input);
        requireContentExisting(input.getId());

        ContentEntity updatedContentEntity = contentRepository.save(contentMapper.dtoToEntity(input));

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

    public List<ContentDto> getContentsByTagName(String tag) {
        return contentRepository.findByTagName(tag).stream().map(contentMapper::entityToDto).toList();
    }

    public ContentDto getContentByTag(UUID tag) {
        List<ContentDto> queryResult = contentRepository.findByTagId(tag).stream().map(contentMapper::entityToDto).toList();
        int size = queryResult.size();
        if (size ==0 ) {
            return null;
        }
        if (size > 1) {
            throw new EntityNotFoundException("Multiple Content found with tag id " + tag);
        }
        return queryResult.get(0);
    }
}
