package de.unistuttgart.iste.gits.content_service.service;

import de.unistuttgart.iste.gits.content_service.persistence.dao.WorkPathEntity;
import de.unistuttgart.iste.gits.content_service.persistence.mapper.WorkPathMapper;
import de.unistuttgart.iste.gits.content_service.persistence.repository.WorkPathRepository;
import de.unistuttgart.iste.gits.generated.dto.*;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class WorkPathService {

    WorkPathMapper workPathMapper;
    WorkPathRepository workPathRepository;

    public WorkPath createWorkPath(String name){
        WorkPathEntity workPathEntity = workPathRepository.save(WorkPathEntity.builder().name(name).build());
        return workPathMapper.entityToDto(workPathEntity);
    }

    public WorkPath updateWorkPath(UpdateWorkPathInput input){

        //updates name only!
        WorkPathEntity workPathEntity = workPathRepository.save(workPathMapper.dtoToEntity(input));
        return workPathMapper.entityToDto(workPathEntity);
    }

    public UUID deleteWorkPath(UUID workPathId){
        requireWorkPathExisting(workPathId);

        workPathRepository.deleteById(workPathId);

        return workPathId;
    }

    public List<WorkPath> getWorkPathByChapterId(UUID uuid){
        List<WorkPathEntity> entities = workPathRepository.findWorkPathEntitiesByChapterId(uuid);

        return entities.stream()
                .map( x -> workPathMapper.entityToDto(x))
                .toList();
    }

    /**
     * Checks if a Work-Path exists.
     *
     * @param uuid The id of the Work-Path to check.
     * @throws EntityNotFoundException If the chapter does not exist.
     */
    private void requireWorkPathExisting(UUID uuid) {
        if (!workPathRepository.existsById(uuid)) {
            throw new EntityNotFoundException("Work-Path with id " + uuid + " not found");
        }
    }

}
