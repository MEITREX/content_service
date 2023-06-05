package de.unistuttgart.iste.gits.content_service.persistence.mapper;

import de.unistuttgart.iste.gits.generated.dto.TagDto;
import de.unistuttgart.iste.gits.generated.dto.CreateTagInputDto;
import de.unistuttgart.iste.gits.generated.dto.UpdateTagInputDto;
import de.unistuttgart.iste.gits.content_service.persistence.dao.TagEntity;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class TagMapper {

    private final ModelMapper modelMapper;

    public TagDto entityToDto(TagEntity TagEntity) {
        // add specific mapping here if needed
        return modelMapper.map(TagEntity, TagDto.class);
    }

    public TagEntity dtoToEntity(TagDto tagDto) {
        // add specific mapping here if needed
        return modelMapper.map(tagDto, TagEntity.class);
    }

    public TagEntity dtoToEntity(CreateTagInputDto tagInputDto) {
        return modelMapper.map(tagInputDto, TagEntity.class);
    }

    public TagEntity dtoToEntity(UpdateTagInputDto input) {
        return modelMapper.map(input, TagEntity.class);
    }
}
