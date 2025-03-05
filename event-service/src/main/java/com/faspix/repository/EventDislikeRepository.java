package com.faspix.repository;

import com.faspix.entity.Event;
import com.faspix.entity.EventDislike;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface EventDislikeRepository extends JpaRepository<EventDislike, Long> {

    @Query("SELECT COUNT(e) FROM EventDislike e " +
            "WHERE e.authorId = :authorId " +
            "AND e.event = :event")
    Integer countDislikes(Event event, String authorId);

    Optional<EventDislike> findEventDislikeByAuthorIdAndEvent(String authorId, Event event);



}
