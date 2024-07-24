package de.unistuttgart.iste.meitrex.content_service.persistence.entity;

import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.util.ArrayList;
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
    @Builder.Default
    private List<ItemEntity> items = new ArrayList<>();
}
