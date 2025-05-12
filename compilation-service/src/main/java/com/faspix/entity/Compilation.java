package com.faspix.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Table(name = "compilations")
public class Compilation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private String title;

    @Column(nullable = false)
    private Boolean pinned;

    @ElementCollection
    @CollectionTable(name = "compilation_events",
            joinColumns = @JoinColumn(name = "compilation_id"))
    @Column(name = "event_id")
    private List<Long> events;

}
