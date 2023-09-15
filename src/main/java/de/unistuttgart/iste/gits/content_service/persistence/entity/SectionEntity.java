package de.unistuttgart.iste.gits.content_service.persistence.entity;

import jakarta.persistence.*;
import lombok.*;

import java.util.Set;
import java.util.UUID;


@Entity(name = "Section")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SectionEntity {

    @Id
    @GeneratedValue
    private UUID id;

    @Column
    private UUID chapterId;

    @Column(nullable = false, length = 255)
    private String name;

    @OneToMany(cascade = CascadeType.ALL, mappedBy = "sectionId")
    Set<StageEntity> stages;
}
