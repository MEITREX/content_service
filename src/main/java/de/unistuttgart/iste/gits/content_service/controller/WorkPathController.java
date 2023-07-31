package de.unistuttgart.iste.gits.content_service.controller;

import de.unistuttgart.iste.gits.content_service.service.WorkPathService;
import de.unistuttgart.iste.gits.generated.dto.StageOrderInput;
import de.unistuttgart.iste.gits.generated.dto.UpdateWorkPathInput;
import de.unistuttgart.iste.gits.generated.dto.WorkPath;
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
public class WorkPathController {

    private final WorkPathService workPathService;

    @MutationMapping
    public WorkPath createWorkPath(@Argument String name){
        return workPathService.createWorkPath(name);
    }
    @MutationMapping
    public WorkPath updateWorkPath(@Argument UpdateWorkPathInput input){
        return workPathService.updateWorkPath(input);
    }

    @MutationMapping
    public UUID deleteWorkPath(@Argument UUID uuid){
        return workPathService.deleteWorkPath(uuid);
    }

    @MutationMapping
    public WorkPath updateStageOrder(@Argument StageOrderInput input){
        return workPathService.reorderStages(input);
    }

    @QueryMapping
    public List<WorkPath> getWorkPathsByChapter(@Argument UUID uuid){
        return workPathService.getWorkPathByChapterId(uuid);
    }

}
