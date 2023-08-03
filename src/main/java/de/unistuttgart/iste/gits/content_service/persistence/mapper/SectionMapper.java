package de.unistuttgart.iste.gits.content_service.persistence.mapper;

import de.unistuttgart.iste.gits.content_service.persistence.dao.SectionEntity;
import de.unistuttgart.iste.gits.generated.dto.Section;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class SectionMapper {

    private final StageMapper stageMapper;

    public Section entityToDto(SectionEntity entity) {

        return Section.builder()
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
