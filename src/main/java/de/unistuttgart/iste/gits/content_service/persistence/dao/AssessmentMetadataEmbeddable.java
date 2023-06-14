package de.unistuttgart.iste.gits.content_service.persistence.dao;

import de.unistuttgart.iste.gits.generated.dto.AssessmentType;
import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Embeddable
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class AssessmentMetadataEmbeddable {

    @Column(nullable = false)
    private int skillPoints;

    @Column(nullable = false)
    private AssessmentType assessmentType;
}
