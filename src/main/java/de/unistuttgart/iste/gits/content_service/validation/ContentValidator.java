package de.unistuttgart.iste.gits.content_service.validation;

import de.unistuttgart.iste.gits.generated.dto.ContentDto;
import de.unistuttgart.iste.gits.generated.dto.CreateContentInputDto;
import de.unistuttgart.iste.gits.generated.dto.UpdateContentInputDto;
import org.springframework.stereotype.Component;

@Component
public class ContentValidator {

    public void validateContentDto(ContentDto contentDto) {
        // add validation logic here
    }

    public void validateCreateContentInputDto(CreateContentInputDto contentInputDto) {
        // TODO implement validation
    }

    public void validateUpdateContentInputDto(UpdateContentInputDto input) {
        // TODO implement validation
    }
}
