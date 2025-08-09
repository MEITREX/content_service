package de.unistuttgart.iste.meitrex.content_service.persistence.entity;


import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Set;
import java.util.UUID;

@Entity(name = "Stage")
@Table(indexes = {
        @Index(name = "idx_stage_section_id", columnList = "section_id")
})
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class StageEntity {

    @Id
    @GeneratedValue
    private UUID id;

    @Column(nullable = false, name = "section_id")
    private UUID sectionId;

    @Column(nullable = false)
    private int position;

    @OneToMany(cascade = CascadeType.PERSIST)
    Set<ContentEntity> requiredContents;

    @OneToMany(cascade = CascadeType.PERSIST)
    Set<ContentEntity> optionalContents;
}
