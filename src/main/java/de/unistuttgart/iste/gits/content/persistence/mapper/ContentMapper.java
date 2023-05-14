package de.unistuttgart.iste.gits.content.persistence.mapper;

import de.unistuttgart.iste.gits.content.dto.ContentDto;
import de.unistuttgart.iste.gits.content.dto.CreateContentInputDto;
import de.unistuttgart.iste.gits.content.dto.UpdateContentInputDto;
import de.unistuttgart.iste.gits.content.persistence.dao.ContentEntity;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ContentMapper {

    private final ModelMapper modelMapper;

    public ContentDto entityToDto(ContentEntity ContentEntity) {
        // add specific mapping here if needed
        return modelMapper.map(ContentEntity, ContentDto.class);
    }

    public ContentEntity dtoToEntity(ContentDto contentDto) {
        // add specific mapping here if needed
        return modelMapper.map(contentDto, ContentEntity.class);
    }

    public ContentEntity dtoToEntity(CreateContentInputDto contentInputDto) {
        return modelMapper.map(contentInputDto, ContentEntity.class);
    }

    public ContentEntity dtoToEntity(UpdateContentInputDto input) {
        return modelMapper.map(input, ContentEntity.class);
    }
}
