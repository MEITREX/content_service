package de.unistuttgart.iste.gits.content_service.persistence.entity;


import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.util.UUID;

@Entity(name = "Skill")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class SkillEntity {
    @Column(name = "skill_id", nullable = false)
    @Id
    @GeneratedValue
    private UUID id;
    @Column(name = "skill_name", nullable = false)
    private String skillName;
}
