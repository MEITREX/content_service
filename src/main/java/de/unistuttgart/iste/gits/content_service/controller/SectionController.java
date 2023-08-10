package de.unistuttgart.iste.gits.content_service.controller;

import de.unistuttgart.iste.gits.content_service.service.SectionService;
import de.unistuttgart.iste.gits.generated.dto.CreateSectionInput;
import de.unistuttgart.iste.gits.generated.dto.Section;
import de.unistuttgart.iste.gits.generated.dto.StageOrderInput;
import de.unistuttgart.iste.gits.generated.dto.UpdateSectionInput;
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
public class SectionController {

    private final SectionService sectionService;

    @MutationMapping
    public Section createSection(@Argument CreateSectionInput input){
        return sectionService.createSection(input);
    }
    @MutationMapping
    public Section updateSection(@Argument UpdateSectionInput input){
        return sectionService.updateSection(input);
    }

    @MutationMapping
    public UUID deleteSection(@Argument UUID id){
        return sectionService.deleteWorkPath(id);
    }

    @MutationMapping
    public Section updateStageOrder(@Argument StageOrderInput input){
        return sectionService.reorderStages(input);
    }

    @QueryMapping
    public List<List<Section>> sectionsByChapterIds(@Argument List<UUID> chapterIds){
        return sectionService.getSectionsByChapterIds(chapterIds);
    }

}
