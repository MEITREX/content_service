package de.unistuttgart.iste.gits.content_service.persistence.entity;

import jakarta.persistence.*;
import lombok.*;

import java.util.*;

@Entity(name = "Tag")
@Table(indexes = {
        @Index(name = "tag_name_index", columnList = "name", unique = true)
})
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TagEntity {

    @Id
    @GeneratedValue
    private UUID id;

    @Column(nullable = false, length = 255, unique = true)
    private String name;

    @EqualsAndHashCode.Exclude
    @ToString.Exclude
    @ManyToMany(mappedBy = "metadata.tags", fetch = FetchType.LAZY,
            cascade = {CascadeType.MERGE, CascadeType.PERSIST, CascadeType.REFRESH})
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
