package de.unistuttgart.iste.meitrex.content_service.persistence.repository;

import de.unistuttgart.iste.meitrex.content_service.persistence.entity.SectionEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Repository for {@link SectionEntity}
 */
@Repository
public interface SectionRepository extends JpaRepository<SectionEntity, UUID> {
    List<SectionEntity> findByChapterIdInOrderByPosition(List<UUID> ids);

    @Query("SELECT MAX(s.position) FROM Section s WHERE s.chapterId = :chapterId")
    Optional<Integer> findHighestPositionByChapterId(@Param("chapterId") UUID chapterId);

    @Query("SELECT s FROM Section s WHERE :stageId IN (SELECT stage.id FROM s.stages stage)")
    Optional<SectionEntity> findSectionEntityByContainedStageId(@Param("stageId") UUID stageId);

    @Query("select section from Section section where section.courseId in (:courseIds)")
    List<SectionEntity> findByCourseIdIn(@Param("courseIds") List<UUID> courseIds);
}
