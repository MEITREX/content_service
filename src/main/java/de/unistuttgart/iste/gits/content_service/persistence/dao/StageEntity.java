package de.unistuttgart.iste.gits.content_service.persistence.dao;


import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Set;
import java.util.UUID;

@Entity(name = "Stage")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class StageEntity {

    @Id
    @GeneratedValue
    private UUID id;

    @Column
    private UUID workPathId;

    @Column(nullable = false)
    private int position;

    @OneToMany
    Set<ContentEntity> requiredContents;

    @OneToMany
    Set<ContentEntity> optionalContent;
}
