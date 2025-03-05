package com.faspix.repository;

import com.faspix.entity.Event;
import com.faspix.entity.EventLikes;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface EventLikeRepository extends JpaRepository<EventLikes, Long> {

    @Query("SELECT COUNT(e) FROM EventLikes e " +
            "WHERE e.authorId = :authorId " +
            "AND e.event = :event")
    Integer countLikes(Event event, String authorId);

}
