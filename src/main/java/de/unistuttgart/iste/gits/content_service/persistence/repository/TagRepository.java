package de.unistuttgart.iste.gits.content_service.persistence.repository;

import de.unistuttgart.iste.gits.content_service.persistence.entity.TagEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface TagRepository extends JpaRepository<TagEntity, UUID> {

    List<TagEntity> findByName(String name);

    @Query("select tag from Tag tag  where tag.name in (:tags)")
    List<TagEntity> findByNameIn(@Param("tags") List<String> tags);

    @Query("select tag from Tag tag join tag.contents content where content.id = :contentId")
    List<TagEntity> findByContentId(@Param("contentId") UUID contentId);

    @Query("select tag from Tag tag join tag.contents content where content.id = :contentId and tag.name = :tagName")
    List<TagEntity> findByContentIdAndTagName(@Param("contentId") UUID contentId, @Param("tagName") String tagName);

    @Query("SELECT tag FROM Tag tag WHERE tag.contents IS EMPTY")
    List<TagEntity> findUnusedTags();
}
