package de.unistuttgart.iste.gits.content_service.controller;

import de.unistuttgart.iste.gits.generated.dto.ContentDto;
import de.unistuttgart.iste.gits.generated.dto.CreateContentInputDto;
import de.unistuttgart.iste.gits.generated.dto.UpdateContentInputDto;
import de.unistuttgart.iste.gits.content_service.persistence.mapper.ContentMapper;
import de.unistuttgart.iste.gits.content_service.service.ContentService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.MutationMapping;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.stereotype.Controller;

import java.util.List;
import java.util.UUID;

@Slf4j
@Controller
@RequiredArgsConstructor
public class ContentController {

    private final ContentService ContentService;
    private final ContentMapper contentMapper;

    @QueryMapping
    public List<ContentDto> contents() {
        log.info("Request for all Contents");
        return ContentService.getAllContents();
    }

    @QueryMapping
    public List<ContentDto> contentsById(@Argument(name = "ids") List<UUID> ids) {
        log.info("Request Contents by Ids");
        return ContentService.getContentsById(ids);
    }

    @MutationMapping
    public ContentDto createContent(@Argument(name = "input") CreateContentInputDto input) {
        return ContentService.createContent(input);
    }

    @MutationMapping
    public ContentDto updateContent(@Argument(name = "input") UpdateContentInputDto input) {
        return ContentService.updateContent(input);
    }

    @MutationMapping
    public UUID deleteContent(@Argument(name = "id") UUID id) {
        return ContentService.deleteContent(id);
    }    
    
}
