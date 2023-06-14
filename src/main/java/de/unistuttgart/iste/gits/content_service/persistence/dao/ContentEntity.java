package de.unistuttgart.iste.gits.content_service.persistence.dao;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import org.hibernate.annotations.DiscriminatorFormula;

import java.util.HashSet;
import java.util.UUID;

@Entity(name = "Content")
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorFormula("case when content_type = 'MEDIA' then 'MEDIA' else 'ASSESSMENT' end")
@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class ContentEntity {

    @Id
    @GeneratedValue
    private UUID id;

    @Embedded
    private ContentMetadataEmbeddable metadata = new ContentMetadataEmbeddable();

    public ContentEntity addToTags(TagEntity tagEntity) {
        if (this.metadata.getTags() == null) {
            this.metadata.setTags(new HashSet<>());
        }
        this.metadata.getTags().add(tagEntity);
        return this;
    }

    public ContentEntity removeFromTags(TagEntity tagEntity) {
        if (this.metadata.getTags() != null) {
            this.metadata.getTags().remove(tagEntity);
        }
        return this;
    }

}
