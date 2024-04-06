package de.unistuttgart.iste.meitrex.content_service.persistence.entity;

import de.unistuttgart.iste.meitrex.generated.dto.BloomLevel;
import jakarta.persistence.*;
import lombok.*;

import java.util.List;
import java.util.UUID;

@Entity(name = "Item")
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ItemEntity {
    @Column(name = "item_id", nullable = false)
    @Id
    private UUID id;
    @ManyToMany(cascade = CascadeType.PERSIST)
    private List<SkillEntity> associatedSkills;
    @Column(name = "associated_bloom_levels", nullable = false)
    private List<BloomLevel> associatedBloomLevels;
}
