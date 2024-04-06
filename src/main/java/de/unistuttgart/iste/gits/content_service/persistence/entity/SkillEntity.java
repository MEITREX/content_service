package de.unistuttgart.iste.meitrex.content_service.persistence.entity;


import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.*;

import java.util.UUID;

@Entity(name = "Skill")
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class SkillEntity {
    @Column(name = "skill_id", nullable = false)
    @Id
    private UUID id;
    @Column(name = "skill_name", nullable = false)
    private String skillName;
}
