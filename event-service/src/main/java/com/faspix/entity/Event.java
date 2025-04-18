package com.faspix.entity;

import com.faspix.enums.EventState;
import com.faspix.utility.Location;
import com.faspix.dto.ResponseUserShortDTO;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.util.List;

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
    private Integer confirmedRequests;

    @Column(nullable = false)
    private OffsetDateTime creationDate;

    @Column(nullable = false)
    private String description;

    @Column(nullable = false)
    private LocalDateTime eventDate;

    @Column(nullable = false)
    private String initiatorId;

    @Embedded
    @Column(nullable = false)
    private Location location;

    @Column(nullable = false)
    private Boolean paid;

    @Column(nullable = false)
    private Integer participantLimit;

    private OffsetDateTime publishedOn;

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
