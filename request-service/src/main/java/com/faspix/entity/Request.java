package com.faspix.entity;

import com.faspix.enums.ParticipationRequestState;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.OffsetDateTime;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "requests")
public class Request {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Long eventId;

    @Column(nullable = false)
    private String requesterId;

    @Column(nullable = false)
    private OffsetDateTime creationDate;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ParticipationRequestState state;

    public Request(Long eventId, String requesterId, OffsetDateTime creationDate) {
        this.eventId = eventId;
        this.requesterId = requesterId;
        this.creationDate = creationDate;
    }
}
