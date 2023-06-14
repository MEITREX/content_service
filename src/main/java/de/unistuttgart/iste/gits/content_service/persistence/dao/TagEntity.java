package de.unistuttgart.iste.gits.content_service.persistence.dao;

import jakarta.persistence.*;
import lombok.*;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

@Entity(name = "Tag")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TagEntity {

    @Id
    @GeneratedValue
    private UUID id;

    @Column(nullable = false, length = 255)
    private String name;

    @EqualsAndHashCode.Exclude
    @ToString.Exclude
    @ManyToMany(mappedBy = "metadata.tags", fetch = FetchType.LAZY)
    private Set<ContentEntity> contents;

    public static TagEntity fromName(String name) {
        TagEntity result = new TagEntity();
        result.name = name;
        return result;
    }

    public TagEntity addToContents(ContentEntity contentEntity) {
        if (this.contents == null) {
            this.contents = new HashSet<>();
            this.contents.add(contentEntity);
        } else {
            this.contents.add(contentEntity);
        }
        return this;
    }

    public TagEntity removeFromContents(ContentEntity contentEntity) {
        if (this.contents != null) {
            this.contents.remove(contentEntity);
        }
        return this;
    }

}
