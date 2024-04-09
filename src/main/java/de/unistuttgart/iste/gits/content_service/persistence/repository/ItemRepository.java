package de.unistuttgart.iste.gits.content_service.persistence.repository;



import de.unistuttgart.iste.gits.content_service.persistence.entity.ItemEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface ItemRepository extends JpaRepository<ItemEntity, UUID> {
    List<ItemEntity> findByAssociatedSkills_Id(UUID skillId);
}
