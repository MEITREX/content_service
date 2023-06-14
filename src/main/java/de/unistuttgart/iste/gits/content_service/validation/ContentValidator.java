package de.unistuttgart.iste.gits.content_service.validation;

import de.unistuttgart.iste.gits.generated.dto.CreateAssessmentInput;
import de.unistuttgart.iste.gits.generated.dto.CreateMediaContentInput;
import de.unistuttgart.iste.gits.generated.dto.UpdateAssessmentInput;
import de.unistuttgart.iste.gits.generated.dto.UpdateMediaContentInput;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class ContentValidator {

    public void validateCreateMediaContentInput(CreateMediaContentInput input) {
        checkNoDuplicateTags(input.getMetadata().getTagNames());
    }

    public void validateUpdateMediaContentInput(UpdateMediaContentInput input) {
        checkNoDuplicateTags(input.getMetadata().getTagNames());
    }

    public void validateCreateAssessmentContentInput(CreateAssessmentInput input) {
        checkNoDuplicateTags(input.getMetadata().getTagNames());
    }

    public void validateUpdateAssessmentContentInput(UpdateAssessmentInput input) {
        checkNoDuplicateTags(input.getMetadata().getTagNames());
    }

    private void checkNoDuplicateTags(List<String> tagNames) {
        long distinctCount = tagNames.stream().distinct().count();
        if (distinctCount != tagNames.size()) {
            throw new IllegalArgumentException("Tags must not contain duplicates");
        }
    }
}
