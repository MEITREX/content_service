package de.unistuttgart.iste.gits.content_service.persistence.entity;

import jakarta.persistence.*;
import lombok.*;


@Entity(name = "Tag")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TagEntity {

    @Id
    @Column(nullable = false, length = 255, unique = true)
    private String name;

    public static TagEntity fromName(String name) {
        TagEntity result = new TagEntity();
        result.name = name;
        return result;
    }

}
