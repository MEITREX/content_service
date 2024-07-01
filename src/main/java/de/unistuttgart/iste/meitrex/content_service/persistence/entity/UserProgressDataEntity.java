package de.unistuttgart.iste.meitrex.content_service.persistence.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

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
    @OrderBy(value = "timestamp DESC")
    @Builder.Default
    private List<ProgressLogItemEmbeddable> progressLog = new ArrayList<>();

    @Column(nullable = true)
    private Integer learningInterval;

    public static final class UserProgressPk implements Serializable {
        private UUID userId;
        private UUID contentId;
    }
}