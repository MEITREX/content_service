package de.unistuttgart.iste.gits.content_service.persistence.repository;

import de.unistuttgart.iste.gits.content_service.persistence.dao.ContentEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface ContentRepository extends JpaRepository<ContentEntity, UUID> {

    List<ContentEntity> findByName(String name);

    @Query("select t.content from Tag t where t.name = :tag")
    List<ContentEntity> findByTagName(@Param("tag") String tag);

    @Query("select t.content from Tag t where t.id = :tagId")
    List<ContentEntity> findByTagId(@Param("tagId") UUID tagId);

    @Query("select content from Content content where content.id in (:ids)")
    List<ContentEntity> findById(List<UUID> ids);

    @Query("select content from Content content where content.chapterId in (:contentIds)")
    List<ContentEntity> findByChapterIds(List<UUID> contentIds);
}
