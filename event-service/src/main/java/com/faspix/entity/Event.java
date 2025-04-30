package com.faspix.entity;

import com.faspix.enums.EventState;
import com.faspix.utility.Location;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import java.time.OffsetDateTime;

@Entity
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "events")
public class Event {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long eventId;

    @Column(nullable = false)
    private String title;

    @Column(nullable = false)
    private String annotation;

    @Column(nullable = false)
    private Long categoryId;

    @Column(nullable = false)
    private String categoryName;

    @Column(nullable = false)
    private Integer confirmedRequests;

    @CreationTimestamp
    @Column(nullable = false)
    private OffsetDateTime creationDate;

    @Column(nullable = false)
    private String description;

    @Column(nullable = false)
    private OffsetDateTime eventDate;

    @Column(nullable = false)
    private String initiatorId;

    @Column(nullable = false)
    private String initiatorUsername;

    @Embedded
    @Column(nullable = false)
    private Location location;

    @Column(nullable = false)
    private Boolean paid;

    @Column(nullable = false)
    private Integer participantLimit;

    private OffsetDateTime publishedAt;

    @Column(nullable = false)
    private Boolean requestModeration;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private EventState state;

    @Column(nullable = false)
    private Integer likes;

    @Column(nullable = false)
    private Integer dislikes;

}
