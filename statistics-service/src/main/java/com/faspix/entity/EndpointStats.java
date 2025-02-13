package com.faspix.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;

@Entity
@Table(name = "endpoint_statistics")
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class EndpointStats {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String app;

    private String uri;

    private String ip;

    private Instant timestamp;
}
