package de.unistuttgart.iste.meitrex.content_service.persistence.repository;

import de.unistuttgart.iste.meitrex.content_service.persistence.entity.SectionEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.*;

/**
 * Repository for {@link SectionEntity}
 */
@Repository
public interface SectionRepository extends JpaRepository<SectionEntity, UUID> {
    List<SectionEntity> findByChapterIdInOrderByPosition(List<UUID> ids);

    @Query("SELECT MAX(s.position) FROM Section s WHERE s.chapterId = :chapterId")
    Optional<Integer> findHighestPositionByChapterId(@Param("chapterId") UUID chapterId);
}
