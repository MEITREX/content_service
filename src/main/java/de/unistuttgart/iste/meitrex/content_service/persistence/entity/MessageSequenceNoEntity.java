package de.unistuttgart.iste.meitrex.content_service.persistence.entity;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;


@Entity(name = "MessageSequenceNoEntity")
@NoArgsConstructor
@SuperBuilder
@Getter
@Setter
@EqualsAndHashCode
@ToString(callSuper = true)
public class MessageSequenceNoEntity {

    @Id
    @Column(name="sequence_no")
    @GeneratedValue
    private Long sequenceNo;

}
