package com.faspix.repository;

import com.faspix.entity.Event;
import com.faspix.enums.EventState;
import com.fasterxml.jackson.annotation.JsonFormat;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.util.List;

public interface EventRepository extends JpaRepository<Event, Long> {

    Page<Event> findEventsByInitiatorId(Long userId, Pageable pageable);

    List<Event> findEventsByCategoryId(Long categoryId);

    // TODO: fix
    @Query("SELECT e FROM Event e WHERE " +
            "e.state = 'PUBLISHED' " +
            "AND (:text IS NULL OR lower(e.annotation) LIKE lower(concat('%', :text, '%')) " +
            "OR lower(e.description) LIKE lower(concat('%', :text, '%'))) " +
            "AND (:categories IS NULL OR e.categoryId IN :categories) " +
            "AND (:paid IS NULL OR e.paid = :paid) " +
            "AND (:rangeStart IS NULL AND e.eventDate >= CURRENT_TIMESTAMP) " +
//            "AND (:rangeStart IS NULL OR e.eventDate >= :rangeStart) " +
//            "AND (:rangeEnd IS NULL OR e.eventDate <= :rangeEnd) " +
            "AND (:onlyAvailable = FALSE OR e.participantLimit = 0 OR e.participantLimit > e.confirmedRequests)")
    Page<Event> searchEvent(@Param("text") String text,
                            @Param("categories") List<Long> categories,
                            @Param("paid") Boolean paid,
                            @Param("rangeStart") LocalDateTime rangeStart,
                            @Param("rangeEnd") LocalDateTime rangeEnd,
                            @Param("onlyAvailable") Boolean onlyAvailable,
                            Pageable pageable);

    @Query("SELECT e FROM Event e WHERE " +
            "(:users IS NULL OR e.initiatorId IN :users) " +
            "AND (:states IS NULL OR e.state IN :states) " +
            "AND (:categories IS NULL OR e.categoryId IN :categories) ")
//            "AND (:rangeStart IS NULL OR e.eventDate >= :rangeStart) ")
//            "AND (:rangeEnd IS NULL OR e.eventDate <= :rangeEnd) " +
    Page<Event> searchEventAdmin(@Param("users") List<Long> users,
                                 @Param("states") List<EventState> states,
                                 @Param("categories") List<Long> categories,
                                 @Param("rangeStart") LocalDateTime rangeStart,
                                 @Param("rangeEnd") LocalDateTime rangeEnd,
                                 Pageable pageable);

}