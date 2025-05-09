package de.unistuttgart.iste.meitrex.content_service.persistence.repository;


import de.unistuttgart.iste.meitrex.common.persistence.MeitrexRepository;
import de.unistuttgart.iste.meitrex.content_service.persistence.entity.ItemEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface ItemRepository extends MeitrexRepository<ItemEntity, UUID> {
    List<ItemEntity> findByAssociatedSkills_Id(UUID skillId);
}