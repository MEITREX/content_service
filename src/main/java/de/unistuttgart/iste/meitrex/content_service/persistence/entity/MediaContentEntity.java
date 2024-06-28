package de.unistuttgart.iste.meitrex.content_service.persistence.entity;

import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import lombok.*;
import lombok.experimental.SuperBuilder;

@Entity(name = "MediaContent")
@DiscriminatorValue("MEDIA")
@NoArgsConstructor
@SuperBuilder
@Getter
@Setter
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class MediaContentEntity extends ContentEntity {
}
