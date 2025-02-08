package com.faspix.entity;

import com.faspix.enums.ParticipationRequestState;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.OffsetDateTime;

@Entity
@Data
@NoArgsConstructor
@Table(name = "requests")
public class Request {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long eventId;

    private Long requesterId;

    private OffsetDateTime creationDate;

    @Enumerated(EnumType.STRING)
    private ParticipationRequestState state;

    public Request(Long eventId, Long requesterId, OffsetDateTime creationDate) {
        this.eventId = eventId;
        this.requesterId = requesterId;
        this.creationDate = creationDate;
    }
}
