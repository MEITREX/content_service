package de.unistuttgart.iste.gits.content_service.validation;

import de.unistuttgart.iste.gits.content_service.persistence.entity.ContentEntity;
import de.unistuttgart.iste.gits.content_service.persistence.repository.ContentRepository;
import de.unistuttgart.iste.gits.generated.dto.*;
import jakarta.validation.ValidationException;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.UUID;

@Component
public class ContentValidator {
    private final ContentRepository contentRepository;

    public ContentValidator(ContentRepository contentRepository) {
        this.contentRepository = contentRepository;
    }

    public void validateCreateMediaContentInput(CreateMediaContentInput input, UUID courseId) {
        if (input.getMetadata().getType() != ContentType.MEDIA) {
            throw new ValidationException("Media content must have type MEDIA");
        }

        // Check if courseId is valid according to validation rules.
        if (!isValidCourseId(courseId)) {
            throw new ValidationException("Invalid courseId");
        }
        checkNoDuplicateTags(input.getMetadata().getTagNames());
    }

    private boolean isValidCourseId(UUID courseId) {
        // Use the ContentRepository to find content entities by courseId.
        List<ContentEntity> contentEntities = contentRepository.findByCourseId(courseId);

        // Check if any contentEntity exists for the given courseId.
        return !contentEntities.isEmpty();
    }


    public void validateUpdateMediaContentInput(UpdateMediaContentInput input) {
        checkNoDuplicateTags(input.getMetadata().getTagNames());
    }

    public void validateCreateAssessmentContentInput(CreateAssessmentInput input, UUID courseId) {
        if (input.getMetadata().getType() == ContentType.MEDIA) {
            throw new ValidationException("MEDIA is not a valid content type for an assessment");
        }
        // Check if courseId is valid according to validation rules.
        if (!isValidCourseId(courseId)) {
            throw new ValidationException("Invalid courseId");
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
