package de.unistuttgart.iste.gits.content_service.persistence.mapper;

import de.unistuttgart.iste.gits.content_service.persistence.dao.WorkPathEntity;
import de.unistuttgart.iste.gits.generated.dto.UpdateWorkPathInput;
import de.unistuttgart.iste.gits.generated.dto.WorkPath;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class WorkPathMapper {

    private final ModelMapper modelMapper;

    public WorkPathEntity dtoToEntity(UpdateWorkPathInput input){
        return modelMapper.map(input, WorkPathEntity.class);
    }

    public WorkPath entityToDto(WorkPathEntity entity){
        return modelMapper.map(entity, WorkPath.class);
    }

}
