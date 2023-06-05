package de.unistuttgart.iste.gits.content_service.persistence.repository;

import de.unistuttgart.iste.gits.content_service.persistence.dao.TagEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface TagRepository extends JpaRepository<TagEntity, UUID> {

    List<TagEntity> findByName(String name);

}
