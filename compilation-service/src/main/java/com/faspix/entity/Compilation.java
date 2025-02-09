package com.faspix.entity;

import com.faspix.dto.ResponseEventShortDTO;
import jakarta.persistence.*;
import lombok.Data;

import java.util.List;

@Entity
@Data
public class Compilation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private String title;

    private Boolean pinned;

    @ElementCollection
    private List<Long> events;

}
