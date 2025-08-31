package de.unistuttgart.iste.meitrex.content_service.controller;

import de.unistuttgart.iste.meitrex.common.user_handling.LoggedInUser;
import de.unistuttgart.iste.meitrex.content_service.service.StageService;
import de.unistuttgart.iste.meitrex.content_service.service.UserProgressDataService;
import de.unistuttgart.iste.meitrex.generated.dto.CreateStageInput;
import de.unistuttgart.iste.meitrex.generated.dto.SectionMutation;
import de.unistuttgart.iste.meitrex.generated.dto.Stage;
import de.unistuttgart.iste.meitrex.generated.dto.UpdateStageInput;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.ContextValue;
import org.springframework.graphql.data.method.annotation.SchemaMapping;
import org.springframework.stereotype.Controller;

import java.util.UUID;

@Slf4j
@Controller
@RequiredArgsConstructor
public class StageController {

    private final StageService stageService;
    private final UserProgressDataService userProgressDataService;

    @SchemaMapping(typeName = "SectionMutation")
    public Stage createStage(@Argument final CreateStageInput input, final SectionMutation sectionMutation) {
        return stageService.createNewStage(sectionMutation.getSectionId(), input);
    }

    @SchemaMapping(typeName = "SectionMutation")
    public Stage updateStage(@Argument final UpdateStageInput input, final SectionMutation sectionMutation) {
        return stageService.updateStage(input);
    }

    @SchemaMapping(typeName = "SectionMutation")
    public UUID deleteStage(@Argument final UUID id, final SectionMutation sectionMutation) {
        return stageService.deleteStage(id);
    }

    @SchemaMapping(field = "requiredContentsProgress")
    public double requiredContentsProgress(final Stage stage, @ContextValue final LoggedInUser currentUser) {
        return userProgressDataService.getStageProgressForUser(stage, currentUser.getId(), true);
    }

    @SchemaMapping(field = "_internal_noauth_requiredContentsProgressForUser")
    public double requiredContentsProgressForUser(final Stage stage, @Argument final UUID userId) {
        return userProgressDataService.getStageProgressForUser(stage, userId, true);
    }

    @SchemaMapping(field = "optionalContentsProgress")
    public double optionalContentsProgress(final Stage stage, @ContextValue final LoggedInUser currentUser) {
        return userProgressDataService.getStageProgressForUser(stage, currentUser.getId(), false);
    }

    @SchemaMapping(field = "_internal_noauth_optionalContentsProgressForUser")
    public double optionalContentsProgressForUser(final Stage stage, @Argument final UUID userId) {
        return userProgressDataService.getStageProgressForUser(stage, userId, false);
    }

    @SchemaMapping(field = "isAvailableToBeWorkedOn")
    public boolean isAvailableToBeWorkedOn(final Stage stage, @ContextValue final LoggedInUser currentUser) {
        return userProgressDataService.isStageAvailableToBeWorkedOn(stage.getId(), currentUser.getId());
    }

    @SchemaMapping(field = "_internal_noauth_isAvailableToBeWorkedOnForUser")
    public boolean isAvailableToBeWorkedOnForUser(final Stage stage, @Argument final UUID userId) {
        return userProgressDataService.isStageAvailableToBeWorkedOn(stage.getId(), userId);
    }
}
