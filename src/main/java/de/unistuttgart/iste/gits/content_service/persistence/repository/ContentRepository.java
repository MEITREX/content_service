package de.unistuttgart.iste.gits.content_service.persistence.repository;

import de.unistuttgart.iste.meitrex.common.persistence.MeitrexRepository;
import de.unistuttgart.iste.gits.content_service.persistence.entity.AssessmentEntity;
import de.unistuttgart.iste.gits.content_service.persistence.entity.ContentEntity;
import de.unistuttgart.iste.gits.content_service.persistence.entity.ItemEntity;
import de.unistuttgart.iste.gits.content_service.persistence.entity.SkillEntity;
import de.unistuttgart.iste.meitrex.generated.dto.SkillType;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

/**
 * Repository for {@link ContentEntity}
 */
@Repository
public interface ContentRepository extends MeitrexRepository<ContentEntity, UUID> {

    @Query("select content from Content content where content.metadata.chapterId in (:chapterIds)")
    List<ContentEntity> findByChapterIdIn(@Param("chapterIds") List<UUID> chapterIds);

    @Query("select content from Content content where content.metadata.courseId in (:courseIds)")
    List<ContentEntity> findByCourseIdIn(@Param("courseIds") List<UUID> courseIds);

    /**
     * Fetches all skill types for content in a chapter.
     *
     * @param chapterId the chapter id
     * @return a list of skill types
     */
    @Query("select assessment.assessmentMetadata.skillTypes from Assessment assessment where assessment.metadata.chapterId = :chapterId")
    List<List<SkillType>> findSkillTypesByChapterId(@Param("chapterId") UUID chapterId);


    @Query("select assessment.items from Assessment assessment where assessment.metadata.chapterId = :chapterId")
    List<ItemEntity> findItemsByChapterId(@Param("chapterId") UUID chapterId);

    @Query("select assessment.items from Assessment assessment where assessment.metadata.courseId = :courseId")
    List<ItemEntity> findItemsByCourseId(@Param("courseId") UUID courseId);
}

