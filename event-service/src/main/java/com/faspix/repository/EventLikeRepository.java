package com.faspix.repository;

import com.faspix.entity.Event;
import com.faspix.entity.EventLike;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface EventLikeRepository extends JpaRepository<EventLike, Long> {

    @Query("SELECT COUNT(e) FROM EventLike e " +
            "WHERE e.authorId = :authorId " +
            "AND e.event = :event")
    Integer countLikes(Event event, String authorId);

    Optional<EventLike> findEventLikeByAuthorIdAndEvent(String authorId, Event event);
    
}
