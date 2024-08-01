package de.unistuttgart.iste.meitrex.content_service.persistence.repository;

import de.unistuttgart.iste.meitrex.content_service.persistence.entity.AssessmentEntity;
import de.unistuttgart.iste.meitrex.common.persistence.MeitrexRepository;

import java.util.UUID;

public interface AssessmentRepository extends MeitrexRepository<AssessmentEntity, UUID> {
    AssessmentEntity findByItems_Id(UUID itemId);

}
