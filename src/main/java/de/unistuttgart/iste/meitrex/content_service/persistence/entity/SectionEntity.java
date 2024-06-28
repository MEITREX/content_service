package de.unistuttgart.iste.meitrex.content_service.persistence.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Set;
import java.util.UUID;


@Entity(name = "Section")
@Table(indexes = {
        @Index(name = "idx_section_chapter_id", columnList = "chapter_id"),
        @Index(name = "idx_section_course_id", columnList = "course_id")
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

    @Column(name = "course_id", nullable = false)
    private UUID courseId;
}
