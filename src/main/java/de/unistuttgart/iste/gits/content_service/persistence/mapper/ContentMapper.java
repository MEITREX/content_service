package de.unistuttgart.iste.gits.content_service.persistence.mapper;

import de.unistuttgart.iste.gits.content_service.persistence.dao.AssessmentEntity;
import de.unistuttgart.iste.gits.content_service.persistence.dao.ContentEntity;
import de.unistuttgart.iste.gits.content_service.persistence.dao.TagEntity;
import de.unistuttgart.iste.gits.generated.dto.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class ContentMapper {

    private final ModelMapper modelMapper;

    public Content entityToDto(ContentEntity contentEntity) {
        Content result;
        if (contentEntity.getMetadata().getType() == ContentType.MEDIA) {
            result = mediaContentEntityToDto(contentEntity);
        } else {
            result = assessmentEntityToDto(contentEntity);
        }

        return result;
    }

    public ContentEntity mediaContentDtoToEntity(CreateMediaContentInput input) {
        return modelMapper.map(input, ContentEntity.class);
    }

    public ContentEntity mediaContentDtoToEntity(UpdateMediaContentInput input) {
        return modelMapper.map(input, ContentEntity.class);
    }

    public MediaContent mediaContentEntityToDto(ContentEntity contentEntity) {
        MediaContent result = modelMapper.map(contentEntity, MediaContent.class);
        if (contentEntity.getMetadata().getTags() != null) {
            var tags = contentEntity.getMetadata().getTags().stream().map(TagEntity::getName).toList();
            result.getMetadata().setTagNames(tags);
        }
        return result;
    }

    public ContentEntity assessmentDtoToEntity(CreateAssessmentInput input) {
        log.info(input.toString());
        AssessmentEntity mapped = modelMapper.map(input, AssessmentEntity.class);
        mapped.getMetadata().setType(input.getContentType());
        log.info(mapped.toString());
        return mapped;
    }

    public ContentEntity assessmentDtoToEntity(UpdateAssessmentInput input, ContentType contentType) {
        return modelMapper.map(input, AssessmentEntity.class);
    }

    public Assessment assessmentEntityToDto(ContentEntity contentEntity) {
        Assessment result;
        if (contentEntity.getMetadata().getType() == ContentType.FLASHCARDS) {
            result = modelMapper.map(contentEntity, FlashcardSetAssessment.class);
            //noinspection ConstantValue
            if (result.getMetadata() == null) {
                //noinspection CastCanBeRemovedNarrowingVariableType
                ((FlashcardSetAssessment) result).setMetadata(new ContentMetadata());
            }
        } else {
            // put other assessment types here
            throw new IllegalArgumentException("Unknown content type");
        }

        if (contentEntity.getMetadata().getTags() != null) {
            var tags = contentEntity.getMetadata().getTags().stream().map(TagEntity::getName).toList();
            result.getMetadata().setTagNames(tags);
        }
        return result;
    }
}
