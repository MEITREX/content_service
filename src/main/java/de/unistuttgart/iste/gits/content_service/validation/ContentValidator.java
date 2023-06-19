package de.unistuttgart.iste.gits.content_service.validation;

import de.unistuttgart.iste.gits.generated.dto.*;
import jakarta.validation.ValidationException;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class ContentValidator {

    public void validateCreateMediaContentInput(CreateMediaContentInput input) {
        if (input.getMetadata().getType() != ContentType.MEDIA) {
            throw new ValidationException("Media content must have type MEDIA");
        }
        checkNoDuplicateTags(input.getMetadata().getTagNames());
    }

    public void validateUpdateMediaContentInput(UpdateMediaContentInput input) {
        checkNoDuplicateTags(input.getMetadata().getTagNames());
    }

    public void validateCreateAssessmentContentInput(CreateAssessmentInput input) {
        if (input.getMetadata().getType() == ContentType.MEDIA) {
            throw new ValidationException("MEDIA is not a valid content type for an assessment");
        }
        checkNoDuplicateTags(input.getMetadata().getTagNames());
    }

    public void validateUpdateAssessmentContentInput(UpdateAssessmentInput input) {
        checkNoDuplicateTags(input.getMetadata().getTagNames());
    }

    private void checkNoDuplicateTags(List<String> tagNames) {
        long distinctCount = tagNames.stream().distinct().count();
        if (distinctCount != tagNames.size()) {
            throw new ValidationException("Tags must not contain duplicates");
        }
    }
}
