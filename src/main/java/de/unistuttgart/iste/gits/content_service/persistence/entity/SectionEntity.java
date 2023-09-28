package de.unistuttgart.iste.gits.content_service.persistence.entity;

import jakarta.persistence.*;
import lombok.*;

import java.util.Set;
import java.util.UUID;


@Entity(name = "Section")
@Table(indexes = {
        @Index(name = "idx_section_chapter_id", columnList = "chapter_id")
})
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SectionEntity {

    @Id
    @GeneratedValue
    private UUID id;

    @Column(nullable = false, name = "chapter_id")
    private UUID chapterId;

    @OrderColumn(nullable = false)
    private int position;

    @Column(nullable = false, length = 255)
    private String name;

    @OneToMany(cascade = CascadeType.ALL, mappedBy = "sectionId")
    Set<StageEntity> stages;
}
