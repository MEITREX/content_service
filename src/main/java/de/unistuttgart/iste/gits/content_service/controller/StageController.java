package de.unistuttgart.iste.gits.content_service.controller;

import de.unistuttgart.iste.gits.common.user_handling.LoggedInUser;
import de.unistuttgart.iste.gits.content_service.service.StageService;
import de.unistuttgart.iste.gits.content_service.service.UserProgressDataService;
import de.unistuttgart.iste.gits.generated.dto.Stage;
import de.unistuttgart.iste.gits.generated.dto.UpdateStageInput;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.ContextValue;
import org.springframework.graphql.data.method.annotation.MutationMapping;
import org.springframework.graphql.data.method.annotation.SchemaMapping;
import org.springframework.stereotype.Controller;

import java.util.UUID;

@Slf4j
@Controller
@RequiredArgsConstructor
public class StageController {

    private final StageService stageService;
    private final UserProgressDataService userProgressDataService;

    @MutationMapping
    public Stage createStage(@Argument UUID sectionId) {
        return stageService.createNewStage(sectionId);
    }

    @MutationMapping
    public Stage updateStage(@Argument UpdateStageInput input) {
        return stageService.updateStage(input);
    }

    @MutationMapping
    public UUID deleteStage(@Argument UUID id) {
        return stageService.deleteStage(id);
    }

    @SchemaMapping(field = "requiredContentsProgress")
    public double requiredContentsProgress(Stage stage, @ContextValue LoggedInUser currentUser) {
        return userProgressDataService.getStageProgressForUser(stage, currentUser.getId(), true);
    }

    @SchemaMapping(field = "optionalContentsProgress")
    public double optionalContentsProgress(Stage stage, @ContextValue LoggedInUser currentUser) {
        return userProgressDataService.getStageProgressForUser(stage, currentUser.getId(), false);
    }

}
