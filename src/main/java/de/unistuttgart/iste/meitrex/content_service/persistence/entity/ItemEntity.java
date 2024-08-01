package de.unistuttgart.iste.meitrex.content_service.persistence.entity;

import de.unistuttgart.iste.meitrex.generated.dto.BloomLevel;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity(name = "Item")
@Data
@SuperBuilder
@AllArgsConstructor
@NoArgsConstructor
public class ItemEntity {
    @Column(name = "item_id", nullable = false)
    @Id
    @GeneratedValue
    private UUID id;
    @ManyToMany(cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    @Builder.Default
    private List<SkillEntity> associatedSkills = new ArrayList<>();
    @Column(name = "associated_bloom_levels", nullable = false)
    @Builder.Default
    private List<BloomLevel> associatedBloomLevels = new ArrayList<>();
}
