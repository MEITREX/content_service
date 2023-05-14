package de.unistuttgart.iste.gits.content.controller;

import de.unistuttgart.iste.gits.content.dto.CreateTagInputDto;
import de.unistuttgart.iste.gits.content.dto.TagDto;
import de.unistuttgart.iste.gits.content.dto.UpdateTagInputDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.MutationMapping;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.stereotype.Controller;
import de.unistuttgart.iste.gits.content.service.TagService;
import de.unistuttgart.iste.gits.content.persistence.mapper.TagMapper;


import java.util.List;
import java.util.UUID;

@Slf4j
@Controller
@RequiredArgsConstructor
public class TagController {

    private final TagService TagService;
    private final TagMapper TagMapper;

    @QueryMapping
    public List<TagDto> tags() {
        log.info("Request for all Tags");
        return TagService.getAllTags();
    }

    @MutationMapping
    public TagDto createTag(@Argument(name = "input") CreateTagInputDto input) {
        return TagService.createTag(input);
    }

    @MutationMapping
    public TagDto updateTag(@Argument(name = "input") UpdateTagInputDto input) {
        return TagService.updateTag(input);
    }

    @MutationMapping
    public UUID deleteTag(@Argument(name = "id") UUID id) {
        return TagService.deleteTag(id);
    }
}


