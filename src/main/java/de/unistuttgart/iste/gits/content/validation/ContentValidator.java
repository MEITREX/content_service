package de.unistuttgart.iste.gits.content.validation;

import de.unistuttgart.iste.gits.content.dto.ContentDto;
import de.unistuttgart.iste.gits.content.dto.CreateContentInputDto;
import de.unistuttgart.iste.gits.content.dto.UpdateContentInputDto;
import org.springframework.stereotype.Component;

@Component
public class ContentValidator {

    public void validateContentDto(ContentDto ContentDto) {
        // add validation logic here
    }

    public void validateCreateContentInputDto(CreateContentInputDto contentInputDto) {
        // TODO implement validation
    }

    public void validateUpdateContentInputDto(UpdateContentInputDto input) {
        // TODO
    }
}
