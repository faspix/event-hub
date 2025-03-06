package com.faspix.repository;

import com.faspix.entity.Event;
import com.faspix.entity.EventLike;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface EventLikeRepository extends JpaRepository<EventLike, Long> {

    Boolean existsByEventAndAuthorId(Event event, String authorId);

    Optional<EventLike> findByAuthorIdAndEvent(String authorId, Event event);
    
}
