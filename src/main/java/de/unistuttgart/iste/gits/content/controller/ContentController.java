package de.unistuttgart.iste.gits.content.controller;

import de.unistuttgart.iste.gits.content.dto.ContentDto;
import de.unistuttgart.iste.gits.content.dto.CreateContentInputDto;
import de.unistuttgart.iste.gits.content.dto.UpdateContentInputDto;
import de.unistuttgart.iste.gits.content.persistence.mapper.ContentMapper;
import de.unistuttgart.iste.gits.content.service.ContentService;
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
    public List<ContentDto> contentsByTag(@Argument(name = "tag") String tag) {
        log.info("Request for all Contents by tag");
        return ContentService.getContentsByTag(tag);
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
