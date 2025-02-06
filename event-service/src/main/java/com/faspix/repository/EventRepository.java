package com.faspix.repository;

import com.faspix.entity.Event;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface EventRepository extends JpaRepository<Event, Long> {

    Page<Event> findEventsByInitiatorId(Long userId, Pageable pageable);

}
