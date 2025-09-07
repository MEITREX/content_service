package de.unistuttgart.iste.meitrex.content_service.persistence.entity;

import de.unistuttgart.iste.meitrex.generated.dto.SkillType;
import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * @implNote fields are nullable because media content does not have them, and we use single table inheritance.
 */
@Embeddable
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class AssessmentMetadataEmbeddable {
    /*
    Note the schema should be changed in the future to be consistent with the GraphQL API.
    Due to avoid DB migrations, we keep the old schema for now.
    TODO: change schema in future to make the skillPoints and skillTypes non-nullable
     */

    @Column(nullable = true)
    private int skillPoints = 0;

    @Column(nullable = true)
    private List<SkillType> skillTypes = List.of();

    @Column(nullable = true)
    private Integer initialLearningInterval = 0;
}
