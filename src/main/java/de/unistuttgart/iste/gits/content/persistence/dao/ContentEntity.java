package de.unistuttgart.iste.gits.content.persistence.dao;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.UUID;

@Entity(name = "Content")
@Table(indexes = {
        @Index(columnList = "contentId")
})
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ContentEntity {

    @Id
    @GeneratedValue
    private UUID id;

    @Column(nullable = false, length = 255)
    private String name;

    @Column(nullable = false)
    private int rewardPoints;

    @Column(nullable = false)
    private boolean workedOn;

    @OneToMany(mappedBy = "content", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<TagEntity> tags;

    @Column(nullable = false)
    private UUID contentId;
}
