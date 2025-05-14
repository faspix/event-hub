package com.faspix.repository;

import com.faspix.entity.EndpointStats;
import com.faspix.shared.dto.ResponseEndpointStatsDTO;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.Instant;
import java.util.List;

public interface StatisticsRepository extends JpaRepository<EndpointStats, Long> {

    @Query("SELECT new com.faspix.shared.dto.ResponseEndpointStatsDTO(e.app, e.uri, COUNT(e.ip)) " +
            " FROM EndpointStats e WHERE " +
            "(e.timestamp >= :start) " +
            "AND (e.timestamp <= :end) " +
            "AND (:uris IS NULL OR e.uri IN :uris) " +
            "GROUP BY e.app, e.uri")
    List<ResponseEndpointStatsDTO> findEndpointStats(
            @Param("start") Instant start,
            @Param("end") Instant end,
            @Param("uris") List<String> uris
    );

    @Query("SELECT new com.faspix.shared.dto.ResponseEndpointStatsDTO(e.app, e.uri, COUNT(DISTINCT(e.ip))) " +
            " FROM EndpointStats e WHERE " +
            "(e.timestamp >= :start) " +
            "AND (e.timestamp <= :end) " +
            "AND (:uris IS NULL OR e.uri IN :uris) " +
            "GROUP BY e.app, e.uri")
    List<ResponseEndpointStatsDTO> findEndpointStatsDistinct(
            @Param("start") Instant start,
            @Param("end") Instant end,
            @Param("uris") List<String> uris
    );

}
