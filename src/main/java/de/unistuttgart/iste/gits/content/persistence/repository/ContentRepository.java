package de.unistuttgart.iste.gits.content.persistence.repository;

import de.unistuttgart.iste.gits.content.persistence.dao.ContentEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface ContentRepository extends JpaRepository<ContentEntity, UUID> {

    List<ContentEntity> findByContentName(String name);

}
