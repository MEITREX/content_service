package de.unistuttgart.iste.gits.content_service.persistence.repository;

import de.unistuttgart.iste.gits.content_service.persistence.dao.WorkPathEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

/**
 * Repository for {@link WorkPathEntity}
 */
@Repository
public interface WorkPathRepository extends JpaRepository<WorkPathEntity, UUID> {

    /**
     * retrieve all Work-Paths for a chapter ID
     * @param chapterId must be non-null and UUID
     * @return all Work-Paths having the given chapter ID
     */
    List<WorkPathEntity> findWorkPathEntitiesByChapterId(UUID chapterId);
}
