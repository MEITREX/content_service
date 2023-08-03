package de.unistuttgart.iste.gits.content_service.persistence.repository;

import de.unistuttgart.iste.gits.content_service.persistence.dao.SectionEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

/**
 * Repository for {@link SectionEntity}
 */
@Repository
public interface SectionRepository extends JpaRepository<SectionEntity, UUID> {

    /**
     * retrieve all Section for a chapter ID
     * @param chapterId must be non-null and UUID
     * @return all Section having the given chapter ID
     */
    List<SectionEntity> findSectionEntitiesByChapterId(UUID chapterId);
}
