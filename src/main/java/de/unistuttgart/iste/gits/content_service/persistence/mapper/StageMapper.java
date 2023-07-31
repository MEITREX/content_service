package de.unistuttgart.iste.gits.content_service.persistence.mapper;

import de.unistuttgart.iste.gits.content_service.persistence.dao.StageEntity;
import de.unistuttgart.iste.gits.generated.dto.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;


@Component
@RequiredArgsConstructor
public class StageMapper {

    private final ContentMapper contentMapper;

    public Stage entityToDto(StageEntity entity){
        return Stage.builder()
                .setId(entity.getId())
                .setPosition(entity.getPosition())
                .setOptionalContents(entity.getOptionalContent()
                        .stream()
                        .map( x-> contentMapper.entityToDto(x))
                        .toList()
                )
                .setRequiredContents(entity.getRequiredContents()
                        .stream()
                        .map( x-> contentMapper.entityToDto(x))
                        .toList()
                )
                .build();
    }
}
