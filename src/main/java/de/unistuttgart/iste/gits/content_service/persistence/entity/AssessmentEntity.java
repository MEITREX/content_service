package de.unistuttgart.iste.meitrex.content_service.persistence.entity;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.util.List;

@Entity(name = "Assessment")
@DiscriminatorValue("ASSESSMENT")
@NoArgsConstructor
@SuperBuilder
@Getter
@Setter
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class AssessmentEntity extends ContentEntity {

    @Embedded
    @Builder.Default
    private AssessmentMetadataEmbeddable assessmentMetadata = new AssessmentMetadataEmbeddable();
    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ItemEntity> items;
}
