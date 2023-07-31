package de.unistuttgart.iste.gits.content_service.controller;

import de.unistuttgart.iste.gits.content_service.service.StageService;
import de.unistuttgart.iste.gits.generated.dto.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.MutationMapping;
import org.springframework.stereotype.Controller;

import java.util.UUID;

@Slf4j
@Controller
@RequiredArgsConstructor
public class StageController {

    private final StageService stageService;

    @MutationMapping
    public Stage createStage(@Argument UUID workPathId){
        return stageService.createNewStage(workPathId);
    }

    @MutationMapping
    public Stage updateStage(@Argument UpdateStageInput input){
        return stageService.updateStage(input);
    }

    @MutationMapping
    public UUID deleteStage(@Argument UUID uuid){
        return stageService.deleteStage(uuid);
    }

}
