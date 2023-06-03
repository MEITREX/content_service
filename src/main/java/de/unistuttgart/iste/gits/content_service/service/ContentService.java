package de.unistuttgart.iste.gits.content_service.service;

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
}
