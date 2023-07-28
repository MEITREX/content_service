package de.unistuttgart.iste.gits.content_service.persistence.repository;

import de.unistuttgart.iste.gits.content_service.persistence.dao.WorkPathEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

/**
 * Repository for {@link WorkPathEntity}
 */
@Repository
public interface WorkPathRepository extends JpaRepository<WorkPathEntity, UUID> {
}
