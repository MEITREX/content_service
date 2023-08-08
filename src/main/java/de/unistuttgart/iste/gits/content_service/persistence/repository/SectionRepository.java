package de.unistuttgart.iste.gits.content_service.persistence.repository;

import de.unistuttgart.iste.gits.content_service.persistence.dao.SectionEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

/**
 * Repository for {@link SectionEntity}
 */
@Repository
public interface SectionRepository extends JpaRepository<SectionEntity, UUID> {
    List<SectionEntity> findByChapterIdIn(List<UUID> ids);
}
