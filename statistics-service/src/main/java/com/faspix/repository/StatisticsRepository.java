package com.faspix.repository;

import com.faspix.entity.EndpointStats;
import org.springframework.data.jpa.repository.JpaRepository;

public interface StatisticsRepository extends JpaRepository<EndpointStats, Long> {
}
