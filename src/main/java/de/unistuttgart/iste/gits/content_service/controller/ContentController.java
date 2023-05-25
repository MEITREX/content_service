package de.unistuttgart.iste.gits.content_service.controller;

import de.unistuttgart.iste.gits.generated.dto.ContentDto;
import de.unistuttgart.iste.gits.generated.dto.CreateContentInputDto;
import de.unistuttgart.iste.gits.generated.dto.UpdateContentInputDto;
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

    private final ContentService contentService;

    @QueryMapping
    public List<ContentDto> contents() {
        log.info("Request for all Contents");
        return contentService.getAllContents();
    }

    @QueryMapping
    public List<ContentDto> contentsById(@Argument(name = "ids") List<UUID> ids) {
        log.info("Request Contents by Ids");
        return contentService.getContentsById(ids);
    }

    @QueryMapping List<List<ContentDto>> contentsByChapterIds(@Argument(name = "chapterIds") List<UUID> chapterIds) {
        log.info("Request Contents by Chapter Ids");
        return contentService.getContentsByChapterIds(chapterIds);
    }

    @MutationMapping
    public ContentDto createContent(@Argument(name = "input") CreateContentInputDto input) {
        return contentService.createContent(input);
    }

    @MutationMapping
    public ContentDto updateContent(@Argument(name = "input") UpdateContentInputDto input) {
        return contentService.updateContent(input);
    }

    @MutationMapping
    public UUID deleteContent(@Argument(name = "id") UUID id) {
        return contentService.deleteContent(id);
    }
}
