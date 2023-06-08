package de.unistuttgart.iste.gits.content_service.persistence.repository;

import de.unistuttgart.iste.gits.content_service.persistence.dao.ContentEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface ContentRepository extends JpaRepository<ContentEntity, UUID> {

    @Query("select content from Content content where content.id in (:ids)")
    List<ContentEntity> findByIdIn(List<UUID> ids);

    @Query("select content from Content content where content.chapterId in (:contentIds)")
    List<ContentEntity> findByChapterIdIn(List<UUID> contentIds);
}
