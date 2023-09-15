package de.unistuttgart.iste.gits.content_service.persistence.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.OrderBy;

import java.io.Serializable;
import java.util.*;

@Entity(name = "UserProgressData")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@IdClass(UserProgressDataEntity.UserProgressPk.class)
public class UserProgressDataEntity {

    @Column(name = "user_id", nullable = false)
    @Id
    private UUID userId;

    @Column(name = "content_id", nullable = false)
    @Id
    private UUID contentId;

    @ElementCollection
    @OrderBy(clause = "timestamp DESC")
    @Builder.Default
    private List<ProgressLogItemEmbeddable> progressLog = new ArrayList<>();

    @Column(nullable = true)
    private Integer learningInterval;

    public static final class UserProgressPk implements Serializable {
        private UUID userId;
        private UUID contentId;
    }
}
