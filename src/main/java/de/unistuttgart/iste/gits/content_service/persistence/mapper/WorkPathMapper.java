package de.unistuttgart.iste.gits.content_service.persistence.mapper;

import de.unistuttgart.iste.gits.content_service.persistence.dao.WorkPathEntity;
import de.unistuttgart.iste.gits.generated.dto.WorkPath;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class WorkPathMapper {

    private final StageMapper stageMapper;

    public WorkPath entityToDto(WorkPathEntity entity) {

        return WorkPath.builder()
                .setId(entity.getId())
                .setChapterId(entity.getChapterId())
                .setName(entity.getName())
                .setStages(
                        entity.getStages()
                                .stream()
                                .map(stageMapper::entityToDto)
                                .toList()
                )
                .build();
    }

}
