package com.faspix.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import java.time.Instant;

@Entity
@Data
@NoArgsConstructor
public class EventDislike {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String authorId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "eventId", referencedColumnName = "eventId", nullable = false)
    private Event event;

    @CreationTimestamp
    @Column(nullable = false)
    private Instant creationDate;

    public EventDislike(String authorId, Event event) {
        this.authorId = authorId;
        this.event = event;
    }

}
