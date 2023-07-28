package de.unistuttgart.iste.gits.content_service.persistence.mapper;

import de.unistuttgart.iste.gits.content_service.persistence.dao.StageEntity;
import de.unistuttgart.iste.gits.content_service.persistence.dao.WorkPathEntity;
import de.unistuttgart.iste.gits.generated.dto.*;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;

import java.util.Set;

@Component
@RequiredArgsConstructor
public class StageMapper {

    private final ModelMapper modelMapper;

    public StageEntity dtoToEntity(CreateStageInput input){
        return modelMapper.map(input, StageEntity.class);
    }

    public Stage entityToDto(StageEntity entity){
        return modelMapper.map(entity, Stage.class);
    }
}
