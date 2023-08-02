package de.unistuttgart.iste.gits.content_service.persistence.dao;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Set;
import java.util.UUID;


@Entity(name = "WorkPath")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class WorkPathEntity {

    @Id
    @GeneratedValue
    private UUID id;

    @Column
    private UUID chapterId;

    @Column(nullable = false, length = 255)
    private String name;

    @OneToMany(cascade = CascadeType.ALL, mappedBy = "workPathId")
    Set<StageEntity> stages;
}
