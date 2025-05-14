package com.faspix.repository;

import com.faspix.domain.entity.Event;
import com.faspix.domain.entity.EventDislike;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface EventDislikeRepository extends JpaRepository<EventDislike, Long> {

    Boolean existsByEventAndAuthorId(Event event, String authorId);

    Optional<EventDislike> findByAuthorIdAndEvent(String authorId, Event event);



}
