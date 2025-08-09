package de.unistuttgart.iste.meitrex.content_service.persistence.repository;

import de.unistuttgart.iste.meitrex.content_service.persistence.entity.ContentEntity;
import de.unistuttgart.iste.meitrex.content_service.persistence.entity.StageEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

/**
 * Repository for {@link StageEntity}
 */
@Repository
public interface StageRepository extends JpaRepository<StageEntity, UUID> {
    /**
     * Returns the stages in which contents with the given IDs are required contents.
     */
    @Query("SELECT DISTINCT s FROM Stage s JOIN s.requiredContents rc WHERE rc.id IN :contentIds")
    List<StageEntity> findByRequiredContentIds(@Param("contentIds") List<UUID> contentIds);

    List<StageEntity> findAllByRequiredContentsContainingOrOptionalContentsContaining(ContentEntity requiredContentEntity, ContentEntity optionalContentEntity);
}
