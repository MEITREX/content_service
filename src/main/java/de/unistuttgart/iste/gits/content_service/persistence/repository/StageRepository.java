package de.unistuttgart.iste.gits.content_service.persistence.repository;

import de.unistuttgart.iste.gits.content_service.persistence.dao.ContentEntity;
import de.unistuttgart.iste.gits.content_service.persistence.dao.StageEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

/**
 * Repository for {@link StageEntity}
 */
@Repository
public interface StageRepository extends JpaRepository<StageEntity, UUID> {


    List<StageEntity> findAllByRequiredContentsContainingOrOptionalContentsContaining(ContentEntity requiredContentEntity, ContentEntity optionalContentEntity);
}
