package de.unistuttgart.iste.meitrex.content_service.persistence.repository;

import de.unistuttgart.iste.meitrex.content_service.persistence.entity.ContentEntity;
import de.unistuttgart.iste.meitrex.content_service.persistence.entity.StageEntity;
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
